window.addEventListener('load', function() {
	getAlarms();
}, false);


function getAlarms(){
	guinness.ajax({
		method:"get",
		url:"/alarms",
		success: function(req) {
			console.log(JSON.parse(req.responseText));
			var noteAlarmList = JSON.parse(req.responseText).note.mapValues;
			var groupAlarmList = JSON.parse(req.responseText).group.mapValues;
			if(noteAlarmList.length + groupAlarmList.length === 0) {
				document.querySelector(".alarm-count").style.display = "none";
				document.querySelector(".alarm-list").style.display = "none";
				if(document.querySelector(".group-card .alarm-count") !== null) {
					document.querySelector(".group-card .alarm-count").style.display = "none";
				}
				return;
			}
			
			document.querySelector(".alarm-count").style.display = "block";
			document.querySelector(".alarm-count").innerHTML = noteAlarmList.length + groupAlarmList.length;
			var alarmListContainer = document.querySelector(".alarm-list");
			alarmListContainer.innerHTML = "";
			var deleteButton = guinness.createElement({
		        name: "button",
		        attrs: {
		            'id': "deleteAlarm"
		        },
		        content: "전체삭제"
		    });
			deleteButton.addEventListener('click', function(){
				guinness.ajax({
			        method:"delete",
			        url:"/alarms/all",
			        success : function(req) {
			        	alarmListContainer.innerHTML = "";
			        	document.querySelector(".alarm-count").style.display = "none";
						document.querySelector(".alarm-list").style.display = "none";
						if(document.querySelector(".group-card .alarm-count") !== null) {
							document.querySelector(".group-card .alarm-count").style.display = "none";
						}
			        }
			    })
			},false);
			alarmListContainer.appendChild(deleteButton);
			for (alarm of groupAlarmList) {
				var alarmContent = "";
				var onclickFunction = "";
				if(alarm.alarmStatus === "I") {
					alarmContent = "<a href='#'>"+alarm.userName+"님이 \""+ alarm.groupName + "\"그룹에 초대하였습니다."+ "</a>";
					onclickFunction = "groupAlarmSelect(this, \""+alarm.calleeId+"\", \""+alarm.groupId+"\", \""+alarm.alarmStatus+"\")";
				}
				if(alarm.alarmStatus === "J") {
					alarmContent = "<a href='#'>"+alarm.userName+"님이 \""+ alarm.groupName + "\"그룹에 가입 요청하였습니다."+ "</a>";
					onclickFunction = "groupAlarmSelect(this, \""+alarm.callerId+"\", \""+alarm.groupId+"\", \""+alarm.alarmStatus+"\", \""+alarm.groupName+"\")";
				}
				alarmListContainer.appendChild(
						guinness.createElement({
							name: "li",
							attrs: {
								id:"alarm-"+alarm.alarmId,
								onclick: onclickFunction
							},
							content: alarmContent
						})
				);
			}
			for (alarm of noteAlarmList) {
				var alarmContent;
				if(alarm.alarmStatus === "N")
					alarmContent = "<a href='#'>"+alarm.userName+"님이 \""+alarm.groupName+"\"에 새 글을 작성하였습니다."+ "</a>";
				if(alarm.alarmStatus === "C")
					alarmContent = "<a href='#'>"+alarm.userName+"님이 "+ "내 글에 댓글을 작성하였습니다."+ "</a>";
				if(alarm.alarmStatus === "P")
						alarmContent = "<a href='#'>"+alarm.userName+"님이 "+ "내 글에 문단 댓글을 작성하였습니다."+ "</a>";
				alarmListContainer.appendChild(
						guinness.createElement({
							name: "li",
							attrs: {
								id:"alarm-"+alarm.alarmId,
								onclick: "noteAlarmSelect(this,"+alarm.noteId+", \""+alarm.alarmStatus+"\")"
							},
							content: alarmContent
						})
				);
			}
		}
	});
}

function noteAlarmSelect(t, noteId, alarmStatus){
	guinness.ajax({
		method:"delete",
		url:"/alarms/note/"+t.id.split("alarm-")[1],
		success: function(req) {
			var result = JSON.parse(req.responseText);
			if (result.success !== true) {
				return;
			}
			if(document.querySelector(".group-card .alarm-count") !== null) {
				loadGroupAlarm();
			}
			getAlarms();
			readNoteContents(noteId);
		}
	});
}
 
function groupAlarmSelect(t, userId, groupId, alarmStatus, groupName){
	guinness.ajax({
		method:"delete",
		url:"/alarms/group/"+t.id.split("alarm-")[1],
		success: function(req) {
			var result = JSON.parse(req.responseText);
			if (result.success !== true) {
				return;
			}
		 	if(alarmStatus === "I"){
		 		guinness.util.alert("그룹 멤버 초대", "초대를 수락하시겠습니까?", function(){
		 			joinGroupMember(userId, groupId, alarmStatus);
		 		}, function(){
		 		});
		 	}
		 	if(alarmStatus === "J"){
		 		guinness.util.alert("\"" +groupName+"\" 그룹 가입 승인", "\"" +userId+"\"의 가입을 승인하시겠습니까?", function(){
		 			joinGroupMember(userId, groupId, alarmStatus);
		 		}, function(){
		 		});
		 	}
		 	getAlarms();
		}
	});	
}

function appendGroupCard(groupId){
	guinness.ajax({
		method : "get",
		url : "/groups",
		success : function(req) {
			var result = JSON.parse(req.responseText);
			if (result.success) {
				var json = result.mapValues;
				for (var i = 0; i < json.length; i++) {
					if(json[i].groupId === groupId){
						appendGroup(json[i]);
						return;
					}
				}
			}
        }
	});
}

function joinGroupMember(userId, groupId, alarmStatus) {
	guinness.restAjax({
			method : "post",
			url : "/groups/members/accept",
			param : "userId=" + userId + "&groupId=" + groupId,
			 statusCode: {
		  			202: function(res) {	// 그룹 초대 승인
		  				if(alarmStatus === 'I'){
							if(document.querySelector("#group-container") !== null){
								appendGroup(JSON.parse(res));
							}
			 				guinness.util.alert("알림", "그룹에 가입되었습니다.");
						}
						if(alarmStatus === 'J'){
			 				guinness.util.alert("알림", "그룹 가입이 승인되었습니다.");
						}
		  			} 
			 }
		});
}

function showQuickAlarm(o){
	var quickAlarm = document.querySelector("#quickAlarm");
	switch (o.type) {
	  case "warn" : quickAlarm.style.backgroundColor="#ffd68d";
	  				quickAlarm.querySelector(".fa").className="fa fa-warning";
	  				break;
	  case "error" : quickAlarm.style.backgroundColor="#ff5a5a";
	  				quickAlarm.querySelector(".fa").className="fa fa-warning";
	  				break;
	  default : quickAlarm.style.backgroundColor="#7cc4b5";
	  			quickAlarm.querySelector(".fa").className="fa fa-leaf";
	  			break;
	}
	quickAlarm.querySelector("#quickAlarmMessage").innerHTML = o.message;
	quickAlarm.style.right = ((document.body.clientWidth - document.querySelector("div.content.wrap").clientWidth ) / 2)+"px";
	quickAlarm.style.display = "block";
	setTimeout(function(){
		quickAlarm.className = "fade";
	}, 1500);
}

var infoMessage = '${infoMessage}';
var errorMessage = '${errorMessage}';

if (errorMessage !== '') {
	showQuickAlarm({type:"error", message:errorMessage});
}
if (infoMessage !== '') {
	showQuickAlarm({type:"info", message:infoMessage});
}