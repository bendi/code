define(["MessageBus", "Dao"], function(mBus, dao) {
  mBus.addEventListener("dao:init", function() {
    var dbSettings = dao.read("settings");
    dbSettingsReady(dbSettings);
  });

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