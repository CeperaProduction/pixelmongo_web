
const gtag_id = 'GTAG-ID';
const ym_id = 'YM-ID';

//INITIALIZATION START

//GOOGLE

$.getScript('https://www.googletagmanager.com/gtag/js?id='+gtag_id);

window.dataLayer = window.dataLayer || [];

function gtag(){
	dataLayer.push(arguments);
}

gtag('js', new Date());
gtag('config', gtag_id);

//YANDEX

(function(m,e,t,r,i,k,a){m[i]=m[i]||function(){(m[i].a=m[i].a||[]).push(arguments)};
m[i].l=1*new Date();k=e.createElement(t),a=e.getElementsByTagName(t)[0],k.async=1,k.src=r,a.parentNode.insertBefore(k,a)})
(window, document, "script", "https://mc.yandex.ru/metrika/tag.js", "ym");

ym(ym_id, "init", {
	clickmap:true,
	trackLinks:true,
	accurateTrackBounce:true,
	webvisor:true
});

//INITIALIZATION END

//EVENT BINDING START

messages.addObserver(function(msg){

	if(msg.text.trim() == 'Вы успешно зарегистрировались'){
		let name = $('.profile-name').text();

		gtag('event', 'sign_up', {
		 	'event_category': 'engagement',
		  	'event_label': name,
		});

		ym(ym_id,'reachGoal','register');

		console.log('metric: register');

	}

	if(msg.text.indexOf('Счет') == 0
		&& msg.text.indexOf('успешно пополнен') != -1
		|| msg.text.indexOf('Пополнение обрабатывается') != -1){

		gtag('event', 'purchase', {
		 	'event_category': 'ecommerce',
		});

		ym(ym_id,'reachGoal','purchase');

		console.log('metric: purchase');

	}

	if(msg.text.indexOf('Набор') == 0
		&& msg.text.indexOf('приобретен на сервере') != -1){

		gtag('event', 'pack', {
		 	'event_category': 'ecommerce',
		});

		ym(ym_id,'reachGoal','pack');

		console.log('metric: pack');

	}

});

$(function(){
	$('.btn-launcher-download').on('click', function(){
		let href = $(this).attr('href');
		let ext = href.substring(href.lastIndexOf('.'), href.length);

		gtag('event', 'launcher', {
		 	'event_category': 'engagement',
		  	'event_label': ext,
		});

		ym(ym_id,'reachGoal','launcher');

		console.log('metric: launcher');
	});
});

//EVENT BINDING END



