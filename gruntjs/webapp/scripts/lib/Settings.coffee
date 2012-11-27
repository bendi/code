define ['jquery', 'cs!lib/MessageBus', 'cs!lib/console', 'cs!events/SettingEvents'], ($, mb, console, SettingEvents) ->
	settings =
		'setting1': 1,
		'setting2': 'some setting'

	mb.addEventListener
		event: SettingEvents.SETTING_UPDATE,
		fn: (event, data) -> set name, value for name,value of data

	set = (name,value) ->
		settings[name] = value
		console.log "setting: " + name + ", " + value + " updated."

	get = (name) -> settings[name]
