<?php

class ElementValidators {

	private $list = array();
	private $elementName;
	private $varName;

	/**
	 *
	 * Enter description here ...
	 * @param string $elementName
	 * @param string $varName
	 */
	public function __construct($elementName, $varName) {
		$this->elementName = $elementName;
		$this->varName = $varName;
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
				list($name, $fn) = $name;
			} else {
				throw new Exception("Wrong parameters for add function");
			}
		}
		$this->list[$name] = $fn;
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
	 *
	 */
	public function __toString() {
		$validators = $this->varName;
		$s = $validators . '.' . $this->elementName .' = []' . "\n";
		foreach($this->list as $fn) {
			$s .= $validators . '.' . $this->elementName .'.push(' . $fn . ');' . "\n";
		}
		return $s;
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
	 *
	 * @param string $content
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

		$req = new Zend_Validate_NotEmpty();
		$jsValidators = array();
		foreach($form->getElements() as $element) {
			$name = $element->getName();
			$label = $element->getLabel();

			$jsValidator = new ElementValidators($name, $this->getVarName());
			foreach($element->getValidators() as $validator) {
				$jsValidator->add($this->getValidator($validator, $label));
			}
			if (!$jsValidator->has('Zend_Validate_NotEmpty') && $element->isRequired()) {
				$jsValidator->add($this->getValidator($req, $label));
			}
			array_push($jsValidators, $jsValidator);
		}

		return $content . '<script type="text/javascript">document.forms.'.$formName.'.onsubmit='.$this->buildSubmitHandler($jsValidators).'</script>';
	}

	/**
	 * @param array $jsValidators
	 */
	protected function buildSubmitHandler(array $jsValidators) {
		$validators = $this->getVarName();
		$s = 'var '.$validators.' = {};' . "\n";
		foreach($jsValidators as $jsValidator) {
			$s .= $jsValidator;
		}
		return 'function(e) {
			'.$s."\n".'
			for(var name in '.$validators.') {
				if (!'.$validators.'[name].length) continue; // skip empty ones
				'.$validators.'[name].sort(function(a,b){return a.ord - b.ord;});
				for(var i = 0; i < '.$validators.'[name].length; i++) {
					try {
						if (!'.$validators.'[name][i].fn.call(this[name])) {
							return false;
						}
					} catch(e) {
						// client made a boo boo - send the form
					}
				}
			}
			return true;
		};';
	}

	const ORDER_REQUIRED 		= 10;
	const ORDER_STRING_LENGTH 	= 20;
	const ORDER_DIGITS 			= 30;
	const ORDER_ALNUM 			= 40;
	const ORDER_REGEX 			= 50;

	protected function getValidator($v, $label) {
		$msgs = $this->getMessages($v);
		$name = get_class($v);
		switch($name) {
			case 'Zend_Validate_StringLength':
				$min = $v->getMin();
				$max = $v->getMax();
				$jsValidation = array(
					'!this.value || this.value.length < ' . $min => $msgs[Zend_Validate_StringLength::TOO_SHORT]
				);
				if ($max != null) {
					$jsValidation['this.value.length > ' . $max] = $msgs[Zend_Validate_StringLength::TOO_LONG];
				}
				$fn = $this->buildFunction(self::ORDER_STRING_LENGTH, $label, $jsValidation);
				break;
			case 'Zend_Validate_Alnum':
				$fn = $this->buildFunction(self::ORDER_ALNUM, $label, array(
					'!this.value' => $msgs[Zend_Validate_Alnum::STRING_EMPTY],
					'!/^\w+$/.test(this.value)' => $msgs[Zend_Validate_Alnum::NOT_ALNUM]
				));
				break;
			case 'Zend_Validate_Regex':
				$pattern = $v->getPattern();
				$fn = $this->buildFunction(self::ORDER_REGEX, $label, array(
					'!this.value || !'.$pattern.'.test(this.value)' => $msgs[Zend_Validate_Regex::NOT_MATCH]
				));
				break;
			case 'Zend_Validate_Digits':
				$fn = $this->buildFunction(self::ORDER_DIGITS, $label, array(
					'!this.value' => $msgs[Zend_Validate_Digits::STRING_EMPTY],
					'/^\d+/.test(this.value)' => $msgs[Zend_Validate_Digits::NOT_DIGITS],
				));
				break;
			case 'Zend_Validate_NotEmpty':
				$fn = $this->buildFunction(self::ORDER_REQUIRED, $label, array(
					'!this.value' => $msgs[Zend_Validate_NotEmpty::IS_EMPTY]
				));
				break;
			default:
				$fn = 'function(){return!0;}';
				break;
		}
		return array($name, $fn);
	}

	protected function buildFunction($ord, $label, $conds) {
		$s = 'function(){';
		foreach($conds as $cond => $msg) {
			$s .= 'if(' . $cond . '){alert("' . $label . ' - ' . $msg . '".replace("%value%", this.value));} else ';
		}
		$s .= '{return!0;}}';
		return '{ord:'.$ord.', fn:'.$s.'}';
	}

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
				$message = str_replace("%$property%", (string) call_user_method('get'.ucfirst($property), $validator), $message);
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