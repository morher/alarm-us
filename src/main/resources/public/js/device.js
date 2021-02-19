var currentUser;

var players = {
	'sofia': {'displayName': 'Sofia', 'color': 'blue'},
	'sebastian': {'displayName': 'Sebastian', 'color': 'red'},
	'sunniva': {'displayName': 'Sunniva', 'color': 'pink'},
	'morten': {'displayName': 'Morten', 'color': 'red'},
	'aneta': {'displayName': 'Aneta', 'color': 'red'},
	'lukas': {'displayName': 'Lukas', 'color': 'blue'},
	'guest1': {'displayName': 'Guest 1', 'color': 'pink'}
};

var rooms = {
	'kitchen': 'Kitchen',
	'bathroom1': 'Batchroom downstairs',
	'familyroom': 'Family room',
	'entrance': 'Entrance',
	'sofia': 'Sofias room',
	'sebastian': 'Sebastians room',
	'sunniva': 'Sunnivas room',
	'understairs': 'Under stairs'
};

var collectedFiles = {
};

var qrScanner;
var qrScannerOnScan;

function scannerOn() {
	if (!qrScanner) {
		return scannerInit(scannerOn);
	}
	qrScanner.scanner.start(qrScanner.cameras[qrScanner.selectedCamera]);
}

function scannerInit(thenDo) {
      Instascan.Camera.getCameras().then(function(cameras) {scannerCallback(cameras, thenDo); });
}

function scannerCallback(cameras, thenDo) {
	if (!qrScanner) {
		var scanner = new Instascan.Scanner({ video: document.getElementById('preview'), mirror: false });
		var selectedCamera = 0;
		var i;
		for (i=0; i<cameras.length; i++) {
			if (cameras[i].name && (cameras[i].name.toLowerCase().indexOf('back') >= 0|| cameras[i].name.toLowerCase().indexOf('bak') >= 0)) {
				selectedCamera = i;
				break;
			}
		}
		qrScanner = {'cameras': cameras, 'selectedCamera': selectedCamera, 'scanner': scanner};
		console.info(qrScanner);
		scanner.addListener('scan', function(data) {
			console.debug(data);
			if (qrScannerOnScan) {
				qrScannerOnScan(data);
			}
		});
	}
	if (thenDo) {
		thenDo();
	}
}

function scannerOff() {
	if (!qrScanner) {
		return;
	}
	window.setTimeout(function() {qrScanner.scanner.stop();}, 0);
}

function doScan(description, icon, urlPrefix, callback) {
	var scanPopup = document.getElementById('scanPopup');
	var scanQuest = document.getElementById('scanQuest');
	scanQuest.innerHTML = '';
	var descriptionNode = document.createElement('p');
	descriptionNode.innerText = description;
	scanQuest.appendChild(descriptionNode);
	if (icon) {
		var iconImg = document.createElement('img');
		iconImg.className = 'icon';
		iconImg.setAttribute('src', icon);
		scanQuest.appendChild(iconImg);
	}
	scanPopup.classList.toggle('show', true);
	scannerOn();
	var stopScan = function stopScan() {
		scanPopup.classList.toggle('show', false);
		scannerOff();
	};
	document.getElementById("scanCancel").onclick = stopScan;
	var scanCallback = function(data) {
		if (data && data.startsWith(urlPrefix)) {
			stopScan();
			callback(data.substring(urlPrefix.length));
		}	
	};
	qrScannerOnScan = scanCallback;
//	window.setTimeout(function() { scanCallback('alarmus://player/sofia'); }, 2000);
}


function signIn() {
	doScan('Scan your access card to sign in', '/images/id-card.svg', 'alarmus://player/', handleSignInScan);
}

function handleSignInScan(userId) {
	if (userId) {
		currentUser = userId;
		updateAuthViews();
		client.sendHook('user-signin-' + userId);
		document.getElementById('signInSound').play();
	}
}

function updateAuthViews() {
	var nodes = document.querySelectorAll('*[data-show-if-signed-in]');
	
	if (currentUser) {
		var player = players[currentUser];
		autofill('currentUser-displayName', player.displayName);
		autofill('currentUser-fig', function(node) { node.setAttribute('src', '/figs/fig-'+player['color']+'.svg'); });
	}
	
	for (i=0; i<nodes.length; i++) {
		var node = nodes[i];
		var ifSignedIn = node.getAttribute('data-show-if-signed-in') == 'true';
		var show = (currentUser != null) == ifSignedIn;
		node.classList.toggle('show', show);
	}
}


function downloadFile() {
	doScan('Connect to a server to download files', '/images/download.svg', 'alarmus://room/', handleDownload);
}

function handleDownload(room) {
	if (room) {
		var roomName = rooms[room];
	
		if (collectedFiles[room]) {
			document.getElementById('fileNotTransferedSound').play();
			
			
		} else {
			var file = document.createElement('div');
			file.className='file';
			file.fromRoom = room;
			file.innerText = 'File from ' + roomName;
			file.onclick = function() {
				uploadFile(file);	
			};
			collectedFiles[room] = file;
			document.getElementById('files').appendChild(file);
			document.getElementById('fileTransferSound').play();
		}	
	}
}

function uploadFile(fileNode) {
	var handleUpload = function(room) {
		if (room) {
			fileNode.remove();
			delete collectedFiles[fileNode.fromRoom];
			client.sendHook('file-transfer-' + fileNode.fromRoom + "-to-" + room + "-by-" + currentUser);
			document.getElementById('fileTransferSound').play();
		}

	};
	doScan('Connect to a server to upload "' + fileNode.innerText + '"', '/images/upload.svg', 'alarmus://room/', handleUpload);
}


window.addEventListener('DOMContentLoaded', function() {
//	signIn();
	updateAuthViews();
});

function signOut() {
	if (confirm("Sign out?")) {
		currentUser = null;
		for (const [key, value] of Object.entries(collectedFiles)) {
			value.remove();
		}
		collectedFiles = {};
		updateAuthViews();
	}
}

function playerDeviceTasksUpdater () {
	console.info(playerTasks);
	var playerTasksNode = document.getElementById('playerTasks');
	if (playerTasksNode && currentUser && playerTasks && playerTasks[currentUser]) {
		var player = playerTasks[currentUser];

		playerTasksNode.innerHTML = '';
		appendTaskSection(playerTasksNode, 'Critical tasks', player.criticalTasks, player, 'critical');	
		appendTaskSection(playerTasksNode, 'Tasks', player.tasks, player);	
		
	}
}