<?php


class ElementValidators {

	private $list = array();
	private $names = array();
	private $elementName;

	/**
	 *
	 * @param string $elementName
	 */
	public function __construct($elementName) {
		$this->elementName = $elementName;
	}

	/**
	 * @param $val
	 * @throws Exception
	 */
	public function add($val) {
		if (empty($val)) {
			return;
		}
		if (func_num_args() == 1) {
			if (is_array($val)) {
				list($name, $ord, $opts, $msgs) = $val;
			} else {
				throw new Exception("Wrong parameters for add function");
			}
		}
		$this->names[$ord] = $name;
		$this->list[$name] = array('opts' => $opts, 'msgs' => $msgs);
	}

	/**
	 *
	 * Enter description here ...
	 * @param string $name
	 */
	public function has($name) {
		return in_array($name, $this->names);
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
		ksort($this->names);
		$ret = array();
		foreach($this->names as $name) {
			$ret[$name] = $this->list[$name];
		}
		return $this->elementName . ':' . Zend_Json::encode($ret);
	}
};

class My_Form_Decorator_JavaScriptValidation extends Zend_Form_Decorator_Abstract {

	private static $instances = 0;
	private static $printValidatorJs = false;

	private $_validateAll = false;
	private $_scriptDir = '/js/';

	public function setValidateAll($validateAll) {
		$this->_validateAll = $validateAll;
	}

	public function getValidateAll() {
		return $this->_validateAll;
	}

	public function setScriptDir($scriptDir) {
		$this->_scriptDir;
	}

	public function getScriptDir() {
		return $this->_scriptDir;
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

		if (!self::$printValidatorJs) {
			$view->headScript()->appendFile($this->getScriptDir() . 'Zend.Form.Validator.js', 'text/javascript');
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

		$validator = sprintf(self::SCRIPT_TPL, $formName, implode(",\n", $jsValidators), Zend_Json::encode((bool)$this->getValidateAll()));

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
		$opts = array();
		switch($name) {
			case 'Zend_Validate_StringLength':
				$min = $validator->getMin();
				$max = $validator->getMax();
				$opts = array('min' => $min);
				$jsMsgs = array('tooShort' => $msgs[Zend_Validate_StringLength::TOO_SHORT]);
				if ($max != null) {
					$opts['max'] = $max;
					$jsMsgs['tooLong'] = $msgs[Zend_Validate_StringLength::TOO_LONG];
				}
				$ord = self::ORDER_STRING_LENGTH;
				break;
			case 'Zend_Validate_Alnum':
				$ord = self::ORDER_ALNUM;
				$jsMsgs = array(
					'empty' => $msgs[Zend_Validate_Alnum::STRING_EMPTY],
					'notAlnum' => $msgs[Zend_Validate_Alnum::NOT_ALNUM]
				);
				break;
			case 'Zend_Validate_Regex':
				$pattern = $validator->getPattern();
				$opts = array('pattern' => $pattern);
				$jsMsgs = array('notMatch' => $msgs[Zend_Validate_Regex::NOT_MATCH]);
				$ord = self::ORDER_REGEX;
				break;
			case 'Zend_Validate_Digits':
				$ord = self::ORDER_DIGITS;
				$jsMsgs = array(
					'empty' => $msgs[Zend_Validate_Digits::STRING_EMPTY],
					'notDigits' => $msgs[Zend_Validate_Digits::NOT_DIGITS]
				);
				break;
			case 'Zend_Validate_NotEmpty':
				$ord = self::ORDER_REQUIRED;
				$jsMsgs = array('isEmpty' => $msgs[Zend_Validate_NotEmpty::IS_EMPTY]);
				break;
			default:
				return null;

		}
		return array($name, $ord, $opts, $jsMsgs);
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
		if (!empty($array['validateAll'])) {
			$this->setValidateAll($array['validateAll']);
		}
		if (!empty($array['scriptDir'])) {
			$scriptDir = rtrim($array['scriptDir'], '/') . '/';
			$this->setScriptDir($scriptDir);
		}
		parent::setOptions($array);
	}
}