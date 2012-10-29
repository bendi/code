define ['underscore'], (_) ->
	log: typeof(console) isnt 'undefined' && _.bind(console.log, console) || () -> null
