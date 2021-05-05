// common variables
var websocket = null;

var knowledge = {
  now: new Date(),
  shirtColor: {red: 255, green: 255, blue: 255}
};

var shirts = [];
var currentShirt = -1;

// common functions

function loadShirt(shirtId) {
  // stop the previous shirt if it has a stop function
  if (typeof stop === 'function') {
    console.log("stopping previous script...");
    stop();
  }

  // remove the previous script tag if it exists
  if (document.getElementById("dynamo")) {
    console.log("removing script tag...");
    document.getElementById("dynamo").remove();
  }
  
  // fetch the HTML of the shirt and load it
  fetch("http://localhost:8080/" + shirtId + "/shirt.html").then(response => response.text()).then(text => {
    document.getElementById("content").innerHTML = text.replace("{ROOT}", "/" + shirtId);
  });
  
  // run the new shirt's script
  let newScript = document.createElement("script");
  newScript.type = "text/javascript";
  newScript.src = "http://localhost:8080/" + shirtId + "/shirt.js";
  newScript.id = "dynamo";
  document.getElementById("content").appendChild(newScript);
}

function nextShirt() {
  if (currentShirt + 1 >= shirts.length) {
    currentShirt = 0;
  } else {
    currentShirt++;
  }

  loadShirt(shirts[currentShirt]);
}

function selectShirt(shirt) {
  if (typeof shirt === "number") loadShirt(shirts[shirt])
  if (typeof shirt === "string") loadShirt(shirt)
}

function setBackgroundColor(r, g, b) {
  document.getElementById("content").style.backgroundColor = `rgb(${r},${g},${b})`;
  shirtColor.red = r;
  shirtColor.blue =  b;
  shirtColor.green = g;
}

// init and connect websocket
document.addEventListener("DOMContentLoaded", function() {
  websocket = new Websocket("ws://localhost:8080/server");
  websocket.connect();
});

// websocket class
class Websocket {
  constructor(url) {
    // set up some local values
    this.ready = false;
    
    this.client = new StompJs.Client({
      brokerURL: url,
      reconnectDelay: 3000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = () => {
      console.log("connected to websocket @ " + url);
      this.ready = true;
      
      // status listener
      this.client.subscribe('/response/status', (message) => {
        let s = JSON.parse(message.body);
        console.log(s);

        knowledge.now = new Date(s.now);
        knowledge.shirtColor = s.shirtColor;

        setBackgroundColor(s.shirtColor.red, s.shirtColor.green, s.shirtColor.blue);
      });

      // shirt listener
      this.client.subscribe('/response/shirts', (message) => {
        shirts = JSON.parse(message.body);
        if (currentShirt === -1) {
          currentShirt = 0;
          loadShirt(shirts[0]);
        }
      });

      // selecter listener
      this.client.subscribe('/response/select', (message) => {
        loadShirt(message.body);
      });

      // get shirts
      this.getShirts();

      this.getStatus();
    };

    this.client.onWebSocketClose = (closeEvent) => {
      console.log("lost connection");
      console.log(closeEvent);
      this.ready = false;
    };

    this.client.onStompError = (err) => {
      console.log("ERROR");
      console.log(err);
    };

    this.client.onError = (err) => {
      console.log('Websocket error: ', err);
    };
  }

  connect() {
    if (this.ready) return;
    this.client.activate();
  }

  disconnect() {
    this.client.deactivate();
  }

  isReady() {
    return this.ready;
  }
  
  // server-side functions
  getStatus() {
    this.client.publish({
      destination: "/app/status"
    });
  }

  getShirts() {
    this.client.publish({
      destination: '/app/shirts'
    });
  }

  // generic server communicator
  send(destination, data) {
    this.client.publish({
      destination: destination,
      body: data
    });
  }
}
