var express = require('express');
var bodyParser = require('body-parser');
var app = express();

// parse application/json
app.use(bodyParser.json());


var data = [{"bla":"bla"},{"bla":"bla"}];

app.get("/data.json", function (req, res) {
  res.send(data);
});

app.put("/data.json", function (req, res) {
  data.push(req.body);
  res.status(200).end();
});


var server = app.listen(3000, function() {
    console.log('Listening on port %d', server.address().port);
});
