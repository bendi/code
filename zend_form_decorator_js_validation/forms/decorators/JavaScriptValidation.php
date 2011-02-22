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
	private static $printValidatorJs = false;

	private $_varName = 'validators';
	private $_validateAll = false;

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

	public function setValidateAll($validateAll) {
		$this->_validateAll = $validateAll;
	}

	public function getValidateAll() {
		return $this->_validateAll;
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
(function(){
var MAX_RETRIES = 10;
Zend = this.Zend || {};
Zend.Form = Zend.Form || {};
Zend.Form.Validator = function(form, rules, validateAll) {
	this.errors = [];
	this.validateAll = validateAll;
	var that = this, retries = 0, init = function(f) {
		if (typeof(form) === 'string') {
			f = document.forms[form];
		}
		if (f) {
			that.form = f;
			if (document.all && !window.opera) {
				f.attachEvent('onsubmit', function(){event.returnValue = that.validate(rules);});
			} else {
				f.addEventListener('submit', function(e) {if (!that.validate(rules)){ e.preventDefault(); }}, false);
			}
		} else if (retries++ < MAX_RETRIES) {
			setTimeout(init, 100);
		}
	}
	init();
};

Zend.Form.Validator.prototype.validateSingle = function(v, el) {
	var ret = true;
	for(var i = 0; i < v.length; i++) {
		try {
			if (!v[i].call(this, el)) {
				ret = false;
				if (!this.validateAll) {
					break;
				}
			}
		} catch(e) {
			// client made a boo boo - send the form
		}
	}
	return ret;
};

Zend.Form.Validator.prototype.validate = function(v) {
	var ret = true;
	for(var name in v) {
		this.errors = [];
		var el = this.form.elements[name];
		if (v[name].length && !this.validateSingle(v[name], el)) {
			ret = false;
		}
		Zend.Form.ErrorReporter.clearErrors(el);
		if (this.errors.length) {
			Zend.Form.ErrorReporter.reportErrors(el, this.errors);
		}
	}
	return ret;
};

Zend.Form.Validator.prototype.addError = function(msg) {
	this.errors.push(msg);
};

if (!Zend.Form.ErrorReporter) {
	Zend.Form.ErrorReporter = {
		reportErrors: function(el, errors) {
			var p = el.parentNode, ul = document.createElement('ul');
			ul.className = 'errors';
			for(var i=0; i<errors.length; i++) {
				var li = document.createElement('li');
				li.innerHTML = errors[i].replace("%value%", el.value);
				ul.appendChild(li);
			}
			p.insertBefore(ul, el.nextSibling);
		},
		clearErrors: function(el) {
			var p = el.parentNode, uls = p.getElementsByTagName('ul');
			if (uls.length) {
				p.removeChild(uls[0]);
			}
		}
	};
}
})()
JS;

		if (!self::$printValidatorJs) {
			$view->headScript()->appendScript($js, 'text/javascript');
			self::$printValidatorJs = true;
		}

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

		$validator = sprintf(self::SCRIPT_TPL, $formName, implode(",\n", $jsValidators), $this->getValidateAll());

		$view->headScript()->appendScript($validator . "\n");

		return $content;
	}

	const SCRIPT_TPL = 'new Zend.Form.Validator("%s", {%s}, %d);';

	const ORDER_REQUIRED 		= 10;
	const ORDER_STRING_LENGTH 	= 20;
	const ORDER_DIGITS 			= 30;
	const ORDER_ALNUM 			= 40;
	const ORDER_REGEX 			= 50;

	/**
	 *
	 * @param Zend_Validate_Interface $validator
	 */
	protected function getValidator(Zend_Validate_Interface $validator) {
		$msgs = $this->getMessages($validator);
		$name = get_class($validator);
		switch($name) {
			case 'Zend_Validate_StringLength':
				$min = $validator->getMin();
				$max = $validator->getMax();
				$jsValidation = array(
					'!el.value || el.value.length < ' . $min => $msgs[Zend_Validate_StringLength::TOO_SHORT]
				);
				if ($max != null) {
					$jsValidation['el.value.length > ' . $max] = $msgs[Zend_Validate_StringLength::TOO_LONG];
				}
				$ord = self::ORDER_STRING_LENGTH;
				$fn = $this->buildFunction($jsValidation);
				break;
			case 'Zend_Validate_Alnum':
				$ord = self::ORDER_ALNUM;
				$fn = $this->buildFunction(array(
					'!el.value' => $msgs[Zend_Validate_Alnum::STRING_EMPTY],
					'!/^\w+$/.test(el.value)' => $msgs[Zend_Validate_Alnum::NOT_ALNUM]
				));
				break;
			case 'Zend_Validate_Regex':
				$pattern = $validator->getPattern();
				$ord = self::ORDER_REGEX;
				$fn = $this->buildFunction(array(
					'!el.value || !'.$pattern.'.test(el.value)' => $msgs[Zend_Validate_Regex::NOT_MATCH]
				));
				break;
			case 'Zend_Validate_Digits':
				$ord = self::ORDER_DIGITS;
				$fn = $this->buildFunction(array(
					'!el.value' => $msgs[Zend_Validate_Digits::STRING_EMPTY],
					'!/^\d+/.test(el.value)' => $msgs[Zend_Validate_Digits::NOT_DIGITS],
				));
				break;
			case 'Zend_Validate_NotEmpty':
				$ord = self::ORDER_REQUIRED;
				$fn = $this->buildFunction(array(
					'!el.value' => $msgs[Zend_Validate_NotEmpty::IS_EMPTY]
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
	 * @param array $conditions
	 */
	protected function buildFunction(array $conditions) {
		$conds = array();
		foreach($conditions as $condition => $msg) {
			$conds[] = sprintf('case %s: return this.addError("%s");', $condition, $msg);
		}
		$conds[] = 'default: return!0;';
		return sprintf('function(el){switch(true){%s}}', implode("\n", $conds));
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
		if (!empty($array['validateAll'])) {
			$this->setValidateAll($array['validateAll']);
		}
		parent::setOptions($array);
	}
}