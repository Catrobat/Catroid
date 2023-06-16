window.webViewUtilsFunctions = {
	isVisible: function (element) {
		const style = getComputedStyle(element);

		if (style.display === 'none') {
			console.log(element, 'display none');
			return false;
		}
		if (style.visibility !== 'visible') {
			console.log(element, 'visibility hidden');
			return false;
		}
		if (element.parentNode && element.parentNode.nodeType === Node.ELEMENT_NODE) {
			console.log('checking parent', element.parentNode);
			return window.webViewUtilsFunctions.isVisible(element.parentNode);
		}

		console.log(element, 'is visible');
		return true;
	},
	waitForElement: function (querySelector, timeout) {
		let found = false;
		const checkInterval = setInterval(function () {
			console.log('waiting for element: ' + querySelector);
			const element = document.querySelector(querySelector);
			if (element) {
				found = true;
				console.log('element found: ' + querySelector);
				clearInterval(checkInterval);
				window.webViewUtils.signalSuccess();
			}
		}, 100);
		setTimeout(function () {
			if (!found) {
				console.log('element not found: ' + querySelector);
				clearInterval(checkInterval);
			}
		}, timeout);
	},
	waitForElementVisibility: function (querySelector, visibility, timeout) {
		const checkInterval = setInterval(function () {
			const element = document.querySelector(querySelector);
			if (element) {
				if (visibility === window.webViewUtilsFunctions.isVisible(element)) {
					clearInterval(checkInterval);
					window.webViewUtils.signalSuccess();
				}
			}
		}, 100);
		setTimeout(function () {
			clearInterval(checkInterval);
		}, timeout);
	},
	isElementVisible: function (querySelector) {
		const element = document.querySelector(querySelector);
		if (element) {
			return window.webViewUtilsFunctions.isVisible(element);
		}
		return false;
	},
	clickElement: function (querySelector) {
		const element = document.querySelector(querySelector);
		if (!element) {
			return false;
		}

		const events = [
			'pointerover',
			'pointerenter',
			'pointerdown',
			'touchstart',
			'pointerup',
			'pointerout',
			'pointerleave',
			'touchend',
			'mouseover',
			'click'
		];

		for (const event of events) {
			const opts = { bubbles: true };
			let firedEvent;
			if (event.includes('touch')) {
				firedEvent = new TouchEvent(event, opts);
			} else if (event.includes('pointer')) {
				firedEvent = new PointerEvent(event, opts);
			} else {
				firedEvent = new MouseEvent(event, opts);
			}

			console.log('Fired Event:', firedEvent);
			element.dispatchEvent(firedEvent);
		}
		return true;
	},
	moveElementByPixels: function (querySelector, directionX, directionY) {
		const element = document.querySelector(querySelector);
		if (!element) {
			return false;
		}

		const events = [
			{ type: 'pointerover', includeCoords: false },
			{ type: 'pointerenter', includeCoords: false },
			{ type: 'pointerdown', includeCoords: false },
			{ type: 'touchstart', includeCoords: false },
			{ type: 'pointermove', includeCoords: true },
			{ type: 'touchmove', includeCoords: true },
			{ type: 'pointerup', includeCoords: true },
			{ type: 'pointerout', includeCoords: true },
			{ type: 'pointerleave', includeCoords: true },
			{ type: 'touchend', includeCoords: true }
		];

		for (const event of events) {
			const { type, includeCoords } = event;
			const opts = { bubbles: true };
			let firedEvent = 'unsupported event';
			if (type.includes('touch')) {
				if (includeCoords) {
					opts.touches = [new Touch({ identifier: 0, target: element, clientX: directionX, clientY: directionY })];
				}
				firedEvent = new TouchEvent(type, opts);
				element.dispatchEvent(firedEvent);
			} else if (type.includes('pointer')) {
				if (includeCoords) {
					opts.clientX = directionX;
					opts.clientY = directionY;
				}
				firedEvent = new PointerEvent(type, opts);
				element.dispatchEvent(firedEvent);
			}

			console.log('Fired Event:', firedEvent);
		}

		return true;
	},
	getBoundingClientRectOfElement: function (querySelector) {
		const element = document.querySelector(querySelector);
		if (!element) {
			return false;
		}

		const rect = element.getBoundingClientRect();
		return { x: rect.x, y: rect.y, width: rect.width, height: rect.height };
	}
};