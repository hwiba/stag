var mousePosition = {};
mousePosition.downPoint = {
    x: 0,
    y: 0
};
mousePosition.upPoint = {
    x: 0,
    y: 0
};

dragBox = {
    top : 0,
    left: 0
}

var pComment = {
    pCommentText: null,
    selectedText: null,
    sameSenCount: 0,
    sameSenIndex: 0,
    userId: null,
    pId: null,
    noteId: null,
};

function selectText() {
    var select = "";
    if (document.getSelection) {
        select = document.getSelection();
    } else if (document.selection) {
        select = document.selection.createRange().text;
    }
    var selectedText = select.toString();
    if (selectedText.length > 0) {
        return selectedText;
    }
    return false;
}

function createPopupPCommentBtn() {
    var templatePopupBtn = document.querySelector("#popupCommentBtnTemplate").text;
    document.body.insertAdjacentHTML("beforeend", templatePopupBtn);
    _createPCommentBox();

    var popupCommentBtn = document.querySelector(".popupCommentBtn");

    mutateObserver(popupCommentBtn);

    popupCommentBtn.addEventListener('click', function (e) {
        e.target.style.display = "none";
        var pCommentBox = document.querySelector(".pCommentBox");
        pCommentBox.style.display = "block";
        pCommentBox.style.top = e.target.style.top;
        pCommentBox.style.left = e.target.style.left;

        pCommentBox.querySelector(".inputP").focus();
        pCommentBox.addEventListener('dragend', dragEnd, false);
    }, false);
}

function mutateObserver (popupCommentBtn) {
    var target = popupCommentBtn;
    var observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.type === "attributes" && mutation.attributeName === "style") {
                if (mutation.target.style.display === "none" ) {
                    var pCommentBoxDisplay = document.body.querySelector(".pCommentBox").style.display;
                    if (pCommentBoxDisplay === "" || pCommentBoxDisplay === "none" ) {
                        document.body.querySelector(".note-content").innerHTML = document.body.querySelector(".hidden-note-content").value;
                    }
                }
            }
        });
    });
    var config = { attributes: true, childList: true, characterData: true };
    observer.observe(target, config);
    //if () {
    //    observer.disconnect();
    //}
}

function _createPCommentBox () {
    var pCommentTemplate = document.querySelector(".pCommentTemplate").text;
    document.body.insertAdjacentHTML("beforeend", pCommentTemplate);
    var pCommentBox = document.body.querySelector(".pCommentBox");

    pCommentBox.querySelector(".setUp").addEventListener("click", createPComment, false);

    pCommentBox.querySelector("#pCommentCancel").addEventListener("click", function (e) {
        e.target.parentElement.parentElement.style.display = "none";
        document.body.querySelector(".inputP").innerText = "";
        document.body.querySelector(".highlighted").className = "none";
        document.body.querySelector(".note-content").innerHTML = document.body.querySelector(".hidden-note-content").value;
    }, false);

}

function createPComment () {
    document.body.querySelector(".pCommentBox").style.display = "none";
    var inputP = document.body.querySelector(".inputP");
    pComment.pCommentText = inputP.innerText;
    inputP.innerText = "";
    document.body.querySelector(".highlighted").className = "none";
    document.body.querySelector(".note-content").innerHTML = document.body.querySelector(".hidden-note-content").value;

    if(pComment.pCommentText.length < 1) {
        return false;
    }

    //TODO pComment 객체를 서버에 전송하여 pComment를 저장할 것.

}

function dragEnd(e) {
    e.preventDefault();
    var elTarget = document.querySelector(".pCommentBox");
    elTarget.style.left = e.clientX + "px";
    elTarget.style.top = e.clientY - e.target.clientHeight + "px";
}

function setPopupPCommentBtn() {
    var elNoteText = document.body.querySelector(".note-content");

    elNoteText.addEventListener('mousedown', function (e) {
        mousePosition.downPoint.x = e.clientX;
        mousePosition.downPoint.y = e.clientY;
    }, false);

    elNoteText.addEventListener('mouseup', function (e) {
        mousePosition.upPoint.x = e.clientX;
        mousePosition.upPoint.y = e.clientY;

        var left = mousePosition.upPoint.x < mousePosition.downPoint.x ? mousePosition.upPoint.x : mousePosition.downPoint.x;
        left += Math.abs(mousePosition.upPoint.x - mousePosition.downPoint.x);
        var top = mousePosition.upPoint.y;

        var elPopupBtn = document.querySelector(".popupCommentBtn");
        var selectedText = selectText();
        var selectedElClass = window.getSelection().getRangeAt(0).commonAncestorContainer;
        if (selectedText && selectedElClass.className !== "note-content") {
            //medium style 코멘트 팝업 버튼 위치 선정. <- 이것이 더 나은지?
            //var selectedRect = window.getSelection().getRangeAt(0).getBoundingClientRect();
            //elPopupBtn.style.top = (selectedRect.top-30) + "px";
            //elPopupBtn.style.left = ((selectedRect.left+selectedRect.right)/2)-31 + "px";
            
            elPopupBtn.style.top = top + "px";
            elPopupBtn.style.left = left + "px";

            elPopupBtn.style.display = "block";
            pComment.selectedText = selectedText;
            pComment.pId = getPid(selectedElClass);
            getSameSentence(pComment, selectedText, window.getSelection());
            getNoteInfo();
        } else {
            elPopupBtn.style.display = "none";
        }
    }, false);
}

function getNoteInfo() {
    pComment.userId = document.querySelector(".hiddenUserId").value;
    pComment.noteId = document.querySelector(".hiddenNoteId").value;
}

function getPid (selectedElClass) {
    while (selectedElClass.tagName !== "P" && selectedElClass.tagName !== "PRE") {
        selectedElClass = selectedElClass.parentNode;
    }
    return selectedElClass.id;
}

function getSameSentence (pComment, selectedText, selection) {
    var selectRange = selection.getRangeAt(0);
    var pId = pComment.pId;
    var pText = document.body.querySelector("#"+pId).innerText;
    var sameIndex = 1;
    var sameTexts = new Array();
    var sameText = pText.indexOf(selectedText);
    selectRange.insertNode(document.createTextNode("`'`ran"));
    var tempText = document.body.querySelector("#"+pId).innerText;
    var searchPrefix = tempText.indexOf("`'`ran");
    selectRange.deleteContents();
    selectRange.insertNode(document.createTextNode(selectedText));
    if (sameText === searchPrefix) {
        pComment.sameSenIndex = sameIndex;
    }
    while(sameText !== -1){
        sameIndex += 1;
        sameTexts.push(sameText);
        sameText = pText.indexOf(selectedText, sameText + selectedText.length);
        if (sameText === searchPrefix) {
            pComment.sameSenIndex = sameIndex;
        }
    }
    pComment.sameSenCount = sameTexts.length;

    //TODO selection에 하일라이팅 하기.
    var span = document.createElement("SPAN");
    span.innerHTML = getSelection();
    span.className = "highlighted";
    selectRange.deleteContents();
    selectRange.insertNode(span);
}