<?php


class ElementValidators {

	private $list = array();
	private $elementName;

	/**
	 *
	 * @param string $elementName
	 */
	public function __construct($elementName) {
		$this->elementName = $elementName;
	}

	/**
	 *
	 * @param string/array $name
	 * @param string $fn
	 * @throws Exception
	 */
	public function add($name, $fn = null) {
		if (func_num_args() == 1) {
			if (is_array($name)) {
				list($name, $ord, $fn) = $name;
			} else {
				throw new Exception("Wrong parameters for add function");
			}
		}
		$this->list[$ord] = $fn;
	}

	/**
	 *
	 * Enter description here ...
	 * @param string $name
	 */
	public function has($name) {
		return !empty($this->list[$name]);
	}

	/**
	 * Shortcut for has method - if name starts with "has"
	 *
	 * @param string $name
	 * @param array $arguments
	 * @throws Exception
	 */
	public function __call($name, $arguments) {
		if (substr($name, 0, 3) == "has") {
			return $this->has("Zend_Validate_" . substr($name, 3));
		}
		if (!method_exists($this, $name)) {
			throw new Exception("Method $name was not found in object: " . get_class($this));
		}
		return call_user_func_array(array($this, $name), $arguments);
	}
	/**
	 *
	 */
	public function __toString() {
		ksort($this->list);
		return $this->elementName .": [ \n". implode(",\n", $this->list) . "\n]";
	}
};

class My_Form_Decorator_JavaScriptValidation extends Zend_Form_Decorator_Abstract {

	private static $instances = 0;

	private $_varName = 'validators';

	/**
	 *
	 * @param string $name
	 */
	public function setVarName($name) {
		$this->_varName = $name;
	}

	/**
	 *
	 */
	public function getVarName() {
		return $this->_varName;
	}

	/**
	 * (non-PHPdoc)
	 * @see Zend_Form_Decorator_Abstract::render()
	 */
	public function render($content) {
		$form = $this->getElement();
		if (null === ($view = $form->getView())) {
			return $content;
		}

		$formName = $form->getName();
		if (!$formName) {
			$formName = get_class($this) . '_form_name_' . self::$instances++;
			$content = str_replace('<form', '<form name="' . $formName . '"', $content);
			$form->setName($formName);
		}

		$js = <<<JS
Zend_Form_Validate = {
	validateSingle: function(v, el) {
		for(var i = 0; i < v.length; i++) {
			try {
				if (!v[i].call(el)) {
					return false;
				}
			} catch(e) {
				// client made a boo boo - send the form
			}
		}
		return true;
	},
	run: function(v) {
		var ret = true;
		for(var name in v) {
			if (v[name].length && !Zend_Form_Validate.validateSingle(v[name], this[name])) {
				ret = false;
			}
		}
		return ret;
	},
	reportError: function(label, msg, el) {
		var p = el.parentNode;
		Zend_Form_Validate.clearError(p);
		var ul = document.createElement('ul');
		ul.className = 'errors';
		var li = document.createElement('li');
		li.innerHTML = msg.replace("%value%", el.value);
		ul.appendChild(li);
		p.insertBefore(ul, el.nextSibling);
	},
	clearError: function(p) {
		var uls = p.getElementsByTagName('ul');
		if (uls.length) {
			p.removeChild(uls[0]);
		}
		return!0;
	}
}
JS;

		$view->headScript()->appendScript($js, 'text/javascript');

		$req = new Zend_Validate_NotEmpty();
		$jsValidators = array();
		foreach($form->getElements() as $element) {
			$elementName = $element->getName();
			$label = $element->getLabel();

			$jsValidator = new ElementValidators($elementName);
			foreach($element->getValidators() as $validator) {
				$jsValidator->add($this->getValidator($validator, $label));
			}
			if (!$jsValidator->hasNotEmpty() && $element->isRequired()) {
				$jsValidator->add($this->getValidator($req, $label));
			}
			array_push($jsValidators, $jsValidator);
		}


		return $content . $this->buildSubmitHandler($formName, $jsValidators);
	}

	const SCRIPT_TPL = '<script type="text/javascript">document.forms.%s.onsubmit=%s</script>';

	/**
	 * @param string $formName
	 * @param array $jsValidators
	 */
	protected function buildSubmitHandler($formName, array $jsValidators) {
		return sprintf(self::SCRIPT_TPL,
			$formName,
			'function(e){return Zend_Form_Validate.run.call(this, {'.implode(",\n", $jsValidators).'});}'
		);
	}

	const ORDER_REQUIRED 		= 10;
	const ORDER_STRING_LENGTH 	= 20;
	const ORDER_DIGITS 			= 30;
	const ORDER_ALNUM 			= 40;
	const ORDER_REGEX 			= 50;

	/**
	 *
	 * @param Zend_Validate_Interface $validator
	 * @param string $label
	 */
	protected function getValidator(Zend_Validate_Interface $validator, $label) {
		$msgs = $this->getMessages($validator);
		$name = get_class($validator);
		switch($name) {
			case 'Zend_Validate_StringLength':
				$min = $validator->getMin();
				$max = $validator->getMax();
				$jsValidation = array(
					'!this.value || this.value.length < ' . $min => $msgs[Zend_Validate_StringLength::TOO_SHORT]
				);
				if ($max != null) {
					$jsValidation['this.value.length > ' . $max] = $msgs[Zend_Validate_StringLength::TOO_LONG];
				}
				$ord = self::ORDER_STRING_LENGTH;
				$fn = $this->buildFunction($label, $jsValidation);
				break;
			case 'Zend_Validate_Alnum':
				$ord = self::ORDER_ALNUM;
				$fn = $this->buildFunction($label, array(
					'!this.value' => $msgs[Zend_Validate_Alnum::STRING_EMPTY],
					'!/^\w+$/.test(this.value)' => $msgs[Zend_Validate_Alnum::NOT_ALNUM]
				));
				break;
			case 'Zend_Validate_Regex':
				$pattern = $validator->getPattern();
				$ord = self::ORDER_REGEX;
				$fn = $this->buildFunction($label, array(
					'!this.value || !'.$pattern.'.test(this.value)' => $msgs[Zend_Validate_Regex::NOT_MATCH]
				));
				break;
			case 'Zend_Validate_Digits':
				$ord = self::ORDER_DIGITS;
				$fn = $this->buildFunction($label, array(
					'!this.value' => $msgs[Zend_Validate_Digits::STRING_EMPTY],
					'!/^\d+/.test(this.value)' => $msgs[Zend_Validate_Digits::NOT_DIGITS],
				));
				break;
			case 'Zend_Validate_NotEmpty':
				$ord = self::ORDER_REQUIRED;
				$fn = $this->buildFunction($label, array(
					'!this.value' => $msgs[Zend_Validate_NotEmpty::IS_EMPTY]
				));
				break;
			default:
				$fn = 'function(){return!0;}';
				$ord = 0;
				break;
		}
		return array($name, $ord, $fn);
	}

	/**
	 *
	 * @param string $label
	 * @param array $conditions
	 */
	protected function buildFunction($label, array $conditions) {
		$conds = array();
		foreach($conditions as $condition => $msg) {
			$conds[] = sprintf('case %s: return Zend_Form_Validate.reportError("%s", "%s", this);', $condition, $label, $msg);
		}
		$conds[] = 'default: return Zend_Form_Validate.clearError(this.parentNode);';
		return sprintf('function(){switch(true){%s}}', implode("\n", $conds));
	}

	/**
	 *
	 * @param Zend_Validate_Interface $validator
	 */
	private function getMessages(Zend_Validate_Interface $validator) {
		$translator = $validator->getTranslator();
		$messageTemplates = $validator->getMessageTemplates();
		$messageVariables = $validator->getMessageVariables();
		$messages = array();
		foreach($messageTemplates as $messageKey => $message) {
			if ($translator) {
				$message = $translator->translate($message);
			}
			foreach ($messageVariables as $property) {
				$message = str_replace("%$property%", call_user_func(array($validator, 'get'.ucfirst($property))), $message);
			}
			$messages[$messageKey] = $message;
		}
		return $messages;
	}

	/**
	 * (non-PHPdoc)
	 * @see Zend_Form_Decorator_Abstract::setOptions()
	 */
	public function setOptions(array $array) {
		if (!empty($array['varName'])) {
			$this->setVarName($array['varName']);
		}
		parent::setOptions($array);
	}
}