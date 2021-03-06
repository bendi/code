/*
 *
 * LICENSE
 *
 * This source file is subject to the new BSD license that is bundled
 * with this package in the file LICENSE.
 *
 * @copyright  Copyright (c) 2010-2011 Marek B�dkowski
 * @license    New BSD License
 *
 * @package Zend.Form.Validator
 */
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

Zend.Form.Validator.prototype.validate = function(v) {
	try {
		var ret = true;
		for(var elementName in v) {
			var el = this.form.elements[elementName];
			var errors = this.validateSingle(v[elementName], el);
			Zend.Form.ErrorReporter.clearErrors(el);
			if (errors.length) {
				Zend.Form.ErrorReporter.reportErrors(el, errors);
				ret = false;
			}
		}
		return ret;
	} catch(e) {
		// smth went wrong - send the form
		return true;
	}
};

Zend.Form.Validator.prototype.validateSingle = function(v, el) {
	var errors = [];
	for(var validatorName in v) {
		var val = v[validatorName], validator = Zend.Form.Validator.Rules[validatorName];
		var msg = validator(el.value, val.opts, val.msgs);
		if (msg !== undefined) {
			ret = false;
			errors.push(msg);
			if (!this.validateAll) {
				break;
			}
		}
	}
	return errors;
};

Zend.Form.Validator.Rules = Zend.Form.Validator.Rules || {};

var Rules = {
	Zend_Validate_StringLength: function(value, opts, msgs) {
		if (!value || value.length < opts.min) {
			return msgs.tooShort;
		} else if (opts.max !== undefined && value.length > opts.max) {
			return msgs.tooLong;
		}
	},

	Zend_Validate_Alnum: function(value, opts, msgs) {
		if (!value) {
			return msgs.empty;
		} else if(!/^\w+$/.test(value)) {
			return msgs.notAlnum;
		}
	},

	Zend_Validate_Regex: function(value, opts, msgs) {
		if (!value || !(new RegExp(opts.pattern)).test(value)) {
			return msgs.notMatch;
		}
	},

	Zend_Validate_Digits: function(value, opts, msgs) {
		if (!value) {
			return msgs.empty;
		} else if(!/^\d+$/.test(value)) {
			return msgs.notDigits;
		}
	},

	Zend_Validate_NotEmpty: function(value, opts, msgs) {
		if (!value) {
			return msgs.isEmpty;
		}
	},

	Zend_Validate_LessThan: function(value, opts, msgs) {
		if (opts.max <= value) {
			return msgs.notLess;
		}
	},

	Zend_Validate_GreaterThan: function(value, opts, msgs) {
		if (opts.max >= value) {
			return msgs.notLess;
		}
	},

	Zend_Validate_Float: function(value, opts, msgs) {
		switch(typeof(value)) {
		case 'string':
		case 'number':
			try {
				parseFloat(value);s
			} catch(e) {
				return msgs.notFloat;
			}
			break;
		default:
			return msgs.invalid;
		}
	},

	Zend_Validate_Between: function(value, opts, msgs) {
		if (opts.inclusive) {
			if (opts.min >= value || value >= opts.max) {
				return msgs.notBetweenStrict;
			}
		} else {
			if (opts.min > value || value > opts.max) {
				return msgs.notBetween;
			}
		}
	}
}

for(var i in Rules) {
	Zend.Form.Validator.Rules[i] = Zend.Form.Validator.Rules[i] || Rules[i];
}

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
