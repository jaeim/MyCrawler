function seetocReady(config, logger) {
	config.setServiceId(SEETOC_SERVICE_ID);
	
	var customParam = {};
	
	var isEmpty = function(val){
		return (val == "" || val == undefined);
	};
	
	if(typeof(_se_p1) != "undefined" && !isEmpty(_se_p1)){
		customParam['_se_p1'] = _se_p1;
	}

	if(typeof(_se_p2) != "undefined" && !isEmpty(_se_p2)){
		customParam['_se_p2'] = _se_p2;
	}

	if(typeof(_se_p3) != "undefined" && !isEmpty(_se_p3)){
		customParam['_se_p3'] = _se_p3;
	}

	if(typeof(_se_p4) != "undefined" && !isEmpty(_se_p4)){
		customParam['_se_p4'] = _se_p4;
	}

	if(typeof(_se_p5) != "undefined" && !isEmpty(_se_p5)){
		customParam['_se_p5'] = _se_p5;
	}

	if(typeof(_se_p6) != "undefined" && !isEmpty(_se_p6)){
		customParam['_se_p6'] = _se_p6;
	}

	if(typeof(_se_p7) != "undefined" && !isEmpty(_se_p7)){
		customParam['_se_p7'] = _se_p7;
	}

	if(_se_sid === 'public.jinhakapply.com') {
		var url = location.protocol + '//' + 'gosi.seoul.go.kr' + location.pathname + location.search;
		logger.getLocationURL = function() { return url; };
		console.log(logger.getLocationURL());
	
	}
	
	logger.sendPageview(customParam);
}

SeeToc.AutoEvent = function() {

	var self = this;

	/**
	 * Event 핸들러
	 * 
	 * Page Load시 발생합니다. url에 메칭된 이벤트를 발생 시킵니다.
	 */ 
	this.sendEvent = function(url) {

	};

	/**
	 * 초기화가 되면서 SeeToc.logger의 핸들러에 등록한다.
	 */
	this.initialize = function() {
		// 핸들어를 등록합니다.
		if (SeeToc == undefined || SeeToc.logger == undefined) {
			setTimeout(self.initialize, 100);
			return;
		}

		SeeToc.logger.sendEvnetHandler = self.sendEvent;

		SeeToc.domReady(function(){seetocReady(SeeToc.config, SeeToc.logger)});
	};

	this.initialize();
};
SeeToc.autoEvent = new SeeToc.AutoEvent;
