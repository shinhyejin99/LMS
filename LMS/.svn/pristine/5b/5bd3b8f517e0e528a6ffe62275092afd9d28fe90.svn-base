document.addEventListener('DOMContentLoaded', function() {
    const calendarEl = document.getElementById('dashboard-calendar');
    const upcomingEventsListEl = document.getElementById('dashboard-upcomingEventsList');

    if (calendarEl) {
	const calendar = new FullCalendar.Calendar(calendarEl, {
	  initialView: 'dayGridMonth',
	  locale: 'ko',
	  height: 'auto',
	  expandRows: true,
	  fixedWeekCount: false,
	  dayMaxEventRows: 2,    // 하루 최대 2줄
	  displayEventTime: false,
	  firstDay: 0,
	  headerToolbar: { left: 'prev', center: 'title', right: 'next' },
	  buttonText: { today:'오늘', month:'월', week:'주', day:'일', list:'목록' },
	  dayCellContent: info => info.date.getDate(),
	  events: { url:'/portal/univcalendar/events', method:'GET' },
	  eventDisplay: 'block',
	  eventsSet: () => updateUpcomingEvents(calendar, upcomingEventsListEl)
	});

        calendar.render();
    }

    function updateUpcomingEvents(calendarInstance, listElement) {
        const currentMonthStart = calendarInstance.getDate();
        const currentMonth = currentMonthStart.getMonth();
        const currentYear = currentMonthStart.getFullYear();
        const allEvents = calendarInstance.getEvents();
        const filteredEvents = allEvents
            .filter(event => {
                const eventStart = event.start;
                return eventStart &&
                    eventStart.getMonth() === currentMonth &&
                    eventStart.getFullYear() === currentYear;
            })
            .sort((a, b) => a.start - b.start);

        if (listElement) {
            listElement.innerHTML = '';
            if (filteredEvents.length === 0) {
                listElement.innerHTML = '<p class="no-events">이번 달에 예정된 주요 일정이 없습니다.</p>';
                return;
            }
            filteredEvents.forEach(event => {
                const startDate = FullCalendar.formatDate(event.start, {
                    month: '2-digit',
                    day: '2-digit',
                    weekday: 'short',
                    locale: 'ko'
                });
                let endDate = '';
                if (event.end &&
                    FullCalendar.formatDate(event.start, { year: 'numeric', month: 'numeric', day: 'numeric' }) !==
                    FullCalendar.formatDate(event.end, { year: 'numeric', month: 'numeric', day: 'numeric' })) {
                    const actualEnd = new Date(event.end.valueOf() - 1);
                    endDate = ' ~ ' + FullCalendar.formatDate(actualEnd, {
                        month: '2-digit',
                        day: '2-digit',
                        weekday: 'short',
                        locale: 'ko'
                    });
                }
                const itemHtml = `
                    <div class="event-item" style="border-left: 5px solid ${event.backgroundColor || '#007bff'};">
                        <span class="event-date">${startDate}${endDate}</span>
                        <span class="event-title">${event.title}</span>
                    </div>
                `;
                listElement.innerHTML += itemHtml;
            });
        }
    }
});