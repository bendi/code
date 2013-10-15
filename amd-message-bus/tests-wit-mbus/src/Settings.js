define(["MessageBus"], function(mBus) {

  mBus.addEventListener("dao:init", dbSettingsReady);

  var settings = readFsSettings();
  settingsReady(settings);

  function readFsSettings(){
	return {};
  }

  function settingReady(key, value) {
    mBus.notify("settingReady:" + key, value);
  }

  function dbSettingsReady(dbSettings) {
   settingsReady(dbSettings);

   settings.__proto__ = dbSettings;
  }

  function settingsReady(s) {
    for(var i in s) {
      settingReady(i, s[i]);
     }
  }

// no direct access to settings!

});