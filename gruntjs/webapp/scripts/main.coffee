require ["jquery", "underscore", "cs!lib/Settings", "cs!lib/MessageBus", 'cs!events/SettingEvents', 'jade!views/start'], ($, _, settings, mb, SettingEvents, start) ->
	$ () ->
		$('body').on 'click', '#us', () ->
			mb.fireEvent type: SettingEvents.SETTING_UPDATE, data: {setting3: "new value"}

		$('body').append(start(date: new Date(), cities: {WAW: "Warszawa", LAX: "Los Angeles"}))


