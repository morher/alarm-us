


function GameClient(nodeName, addr) {
	var messageListeners = [];
	var statusListeners = [];
	var enabled = true;
	var websocket = null;
	var client = this;
	var heartbeat = null;

	this.disconnect = function() {
		enabled = false;
		if (websocket && websocket.readyState == WebSocket.OPEN) {
			websocket.close();
		}
		websocket = null;
	}

	var updateStatus = function(newStatus) {
		console.info("GameClient status: " + newStatus);
		for (var i = 0; i < statusListeners.length; i++) {
			statusListeners[i](newStatus, client);
		}
	}

	this.connect = function() {
		this.disconnect();
		enable = true;
		updateStatus("CONNECTING");
		websocket = new WebSocket(addr);
		websocket.onopen = function(evt) {
			updateStatus("ONLINE");
		};
		websocket.onclose = function(evt) {
			if (heartbeat) {
				window.clearInterval(heartbeat);
				heartbeat = null;
			}
			updateStatus("OFFLINE");
		};
		websocket.onmessage = function(evt) {
			console.info('packet: ' + evt.data);
			var packet = JSON.parse(evt.data);
			if (packet.message) {
				for (var i = 0; i < messageListeners.length; i++) {
					messageListeners[i](packet.message);
				}
			}
		};
		heartbeat = window.setInterval(function() { websocket.send("{}") }, 5000);
	}

	this.addStatusListener = function(callback) {
		statusListeners.push(callback);
	}

	this.addMessageListener = function(callback) {
		messageListeners.push(callback);
	}

	this.sendMessage = function(message) {
		var packet = { 'message': message };
		websocket.send(JSON.stringify(packet));
	}
	
	this.sendHook = function(hook) {
		this.sendMessage({'hook': hook});
	}
}

function autofill(autofillName, nodeCallback) {
	var nodes = document.querySelectorAll('*[data-autofill="' + autofillName + '"]');
	for (n=0; n<nodes.length; n++) {
		if (typeof nodeCallback === 'function') {
			nodeCallback(nodes[n]);		
		} else {
			nodes[n].innerHTML = nodeCallback;
		}
	}
}

function reconnecter(status, client) {
	console.info(client);
	if (status == 'OFFLINE') {
		window.setTimeout(function() { client.connect(); }, 1000);
	}
}

var gamePhase;

function gamePhaseUpdater(message) {
	if (message.phase) {
		gamePhase = message.phase;
	}
	updateViews();
}

function updateViews() {
	var nodes = document.querySelectorAll('.conditional,*[data-show-in-phases],*[data-hide-in-phases]');
	for (i=0; i<nodes.length; i++) {
		var node = nodes[i];
		var view = false;
		var viewInPhasesAttr = node.getAttribute('data-show-in-phases');
		if (viewInPhasesAttr) {
			view = viewInPhasesAttr.split(',').includes(gamePhase);
		}
		var hideInPhasesAttr = node.getAttribute('data-hide-in-phases');
		if (hideInPhasesAttr) {
			view = !hideInPhasesAttr.split(',').includes(gamePhase);
		}
		node.classList.toggle('view', view);
	}
}

function gameViewUpdater(message) {
	if (message.attributes) {
		var nodes = document.querySelectorAll('*[data-show-attribute]');
		for (i=0; i<nodes.length; i++) {
			var node = nodes[i];
			var showAttribute = node.getAttribute('data-show-attribute');
			if (showAttribute) {
				var attrValue = message.attributes[showAttribute];
				if (attrValue) {
					var view = node.getAttribute('data-show-if-value').split(',').includes(attrValue);
					node.classList.toggle('view', view);
				}
			}
		}
	}
}

function soundsListener(message) {
	if (message.sceneActions) {
		var nodes = document.querySelectorAll('audio[data-play-on-action]');
		for (i=0; i<nodes.length; i++) {
			var node = nodes[i];
			var playOnActions = node.getAttribute('data-play-on-action').split(',');
			var play = false;
			for (a=0; a<playOnActions.length; a++) {
				if (message.sceneActions.includes(playOnActions[a])) {
					play = true;
				}
			}
			if (play) {
				node.play();
			}
		}
	}
}

var gameTimeout = null;

function formatTimeLeft(millis) {
	if (millis > 0) {
		var hours = Math.floor(millis / (60 * 60 * 1000));
		var minutes = Math.floor((millis % (60 * 60 * 1000)) / (60 * 1000));
		var seconds = Math.floor((millis % (60 * 1000)) / 1000);

		return (hours ? hours + ':' : '')
			+ (hours && minutes < 10 ? '0' : '') + minutes + ':'
			+ (seconds < 10 ? '0' : '') + seconds;
	}
	return '0:00';
}

function gameTimeoutListener(message) {
	if (message.attributes
		&& message.attributes.gameTimeout) {
		
		gameTimeout = Date.parse(message.attributes.gameTimeout);
		updateTimeLeft();
	}
}

var gameTimeoutUpdateInterval = null;

function updateTimeLeft() {
	var enable = gameTimeout > 0;
	if (enable) {
		var now = new Date().getTime();
		var millis = gameTimeout - now;
		var timeLeft = formatTimeLeft(millis);

		autofill('gameTimeout', function(node) { node.innerText = timeLeft });

		enable = millis > 0;
	}
	if (enable && !gameTimeoutUpdateInterval) {
		gameTimeoutUpdateInterval = window.setInterval(updateTimeLeft, 1000);

	} else if (!enable && gameTimeoutUpdateInterval) {
		window.clearInterval(gameTimeoutUpdateInterval);
		gameTimeoutUpdateInterval = null;
	}
}

var playerTasks = null;

function playerTasksListener(message) {
	if (message.players) {
		updatePlayers(message.players);
		updateTaskCounter(message.players);
		playerTasks = {};
		for (i=0; i<message.players.length;i++) {
			playerTasks[message.players[i].id] = message.players[i];
		}
		if (playerDeviceTasksUpdater) {
			playerDeviceTasksUpdater();
		}
	}
}


function updatePlayers(players) {
	var nodes = document.querySelectorAll('*[data-autofill="playersAndTasks"]');

	for (i=0; i<nodes.length; i++) {
		recreatePlayersAndTasks(nodes[i], players);
	}
	
}

function recreatePlayersAndTasks(listNode, players) {
	listNode.innerHTML = '';
	for (p=0; p<players.length; p++) {
		listNode.appendChild(createPlayerNode(players[p]));
	}
}

function createPlayerNode(player) {
	var playerNode = document.createElement('div');
	playerNode.className = 'player';
	playerNode.playerId = player.id;
	
	var playerNameNode = document.createElement('h2');
	playerNode.appendChild(playerNameNode);

	var color = player.color ? player.color : 'red';

	var playerFig = document.createElement('img');
	playerFig.setAttribute('src', 'figs/fig-' + color + '.svg');
	playerFig.className = 'inlineFig';
	playerNameNode.appendChild(playerFig);
	playerNameNode.appendChild(document.createTextNode(player.displayName));
	
	appendTaskSection(playerNode, 'Critical tasks', player.criticalTasks, player, 'critical');	
	appendTaskSection(playerNode, 'Tasks', player.tasks, player);	
	
	return playerNode;
}

function appendTaskSection(node, title, tasks, player, className='') {
	if (tasks && tasks.length) {
		var sectionNode = document.createElement('dev');
		sectionNode.className = className;
		var tasksTitleNode = document.createElement('h3');
		tasksTitleNode.innerText = title;
		sectionNode.appendChild(tasksTitleNode);
		sectionNode.appendChild(createTaskList(tasks, player));
		node.appendChild(sectionNode);
	}
}

function createTaskList(tasks, player) {
	var tasksListNode = document.createElement('ul');
	tasksListNode.className='tasks';
	
	for (i=0; i<tasks.length; i++) {
		var task = tasks[i];
		var taskNode = document.createElement('li');
		if (task.icon) {
			var iconNode = document.createElement('span');
			iconNode.className = 'icon';
			iconNode.innerText = task.icon;
			taskNode.appendChild(iconNode);
		}
		taskNode.appendChild(document.createTextNode(' ' + task.name));
		taskNode.classList.toggle('completed', task.completed);
		taskNode.player = player;
		taskNode.task = task;
		if (task.type == 'manual') {
			taskNode.onclick = taskClickHandler;
		} else {
			taskNode.ondblclick = taskClickHandler;
		}
		tasksListNode.appendChild(taskNode);
	}

	return tasksListNode;
}

function updateTaskCounter(players) {
	var tasksTotal = 0;
	var tasksCompleted = 0;
	for (p=0; p<players.length; p++) {
		var player = players[p];
		for (t=0; t<player.criticalTasks.length; t++) {
			tasksTotal++;
			if (player.criticalTasks[t].completed) {
				tasksCompleted++;
			}
		}
		for (t=0; t<player.tasks.length; t++) {
			tasksTotal++;
			if (player.tasks[t].completed) {
				tasksCompleted++;
			}
		}
	}
	var percent = tasksCompleted * 100 / tasksTotal;
	autofill('taskCountWidth', function(node) { node.style.width=percent + '%'});
}


var confirmTaskTimeout = null;

function taskClickHandler() {
	var nodes = document.querySelectorAll('*[data-autofill="confirmTask"]');
	for (i=0; i<nodes.length; i++) {
		updateConfirmTask(nodes[i], this.player, this.task);
	}
	
	
	selectWindow('confirm-task');
	confirmTaskTimeout = window.setTimeout(closeConfirmTask, 10000);
}

function updateConfirmTask(node, player, task) {
	node.innerHTML = '';
	
	var playerNameNode = document.createElement('h2');
	playerNameNode.innerText = player.displayName;
	node.appendChild(playerNameNode);
	
	var taskNode = document.createElement('p');
	taskNode.innerText = task.name;
	node.appendChild(taskNode);
	
	var buttonsNode = document.createElement('p');
	buttonsNode.className = 'center';
	node.appendChild(buttonsNode);
	
	var confirmButton = document.createElement('button');
	confirmButton.innerText = 'Confirm!';
	confirmButton.className = 'yes';
	confirmButton.onclick = function() {
		client.sendHook('completeTask:' + task.id);
		closeConfirmTask();
	};
	buttonsNode.appendChild(confirmButton);

	var cancelButton = document.createElement('button');
	cancelButton.innerText = 'Not quite yet...';
	cancelButton.className = 'no';
	cancelButton.onclick = closeConfirmTask;
	buttonsNode.appendChild(cancelButton);
}

function closeConfirmTask() {
	if (confirmTaskTimeout) {
		window.clearTimeout(confirmTaskTimeout);
		confirmTaskTimeout = null;
	}
	selectWindow('tasks-screen');
}


function selectWindow(windowId) {
	var windows = document.getElementsByClassName('window');
	for (w=0; w<windows.length; w++) {
		windows[w].classList.toggle('show', false);
	}
	
	var win = document.getElementById(windowId);
	if (win) {
		win.classList.toggle('show', true);
	}
}

var socketUrl = window.location.origin.replace(/^http/, 'ws') + '/api/socket';

client = new GameClient('WebTest', socketUrl);
client.addStatusListener(reconnecter);
client.addMessageListener(gamePhaseUpdater);
client.addMessageListener(gameViewUpdater);
client.addMessageListener(gameTimeoutListener);
client.addMessageListener(soundsListener);
client.addMessageListener(playerTasksListener);



window.addEventListener('DOMContentLoaded', function() {
	client.connect();
	selectWindow(document.body.getAttribute('data-default-window'));
});


