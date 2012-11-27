define ['jquery', 'underscore'], ($, _) ->

	dispatcher = $({})

	class MessageBus
		addEventListener: (p) =>
			fn = if p.ctx && typeof p.ctx is 'object'
					_.bind(fn, p.ctx)
				else
					p.fn
			dispatcher.on(p.event, fn)

		fireEvent: (event, data) =>
			type = event
			if typeof event is 'object'
				[type, data] = [event.type, event.data]

			dispatcher.trigger(type, data);

	new MessageBus()
