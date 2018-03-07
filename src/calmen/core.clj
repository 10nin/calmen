(ns calmen.core
  (:require [clojure.string :as str]
            [net.cgrand.enlive-html :as html]
            [selmer.parser :as tmpl]))

(def ^:dynamic *CALENDAR-TEMPLATE* "
BEGIN:VCALENDAR
METHOD:PUBLISH
VERSION:2.0
PRODID:CALMEN-CLJ
X-WR-TIMEZONE:Asia/Tokyo
CALSCALE:GREGORIAN
BEGIN:VTIMEZONE
TZID:Asia/Tokyo
BEGIN:DAYLIGHT
TZOFFSETFROM:+0900
DTSTART:19500507T000000
TZNAME:JDT
TZOFFSETTO:+1000
END:DAYLIGHT
BEGIN:STANDARD
TZOFFSETFROM:+1000
DTSTART:19510909T000000
TZNAME:JST
TZOFFSETTO:+0900
END:STANDARD
END:VTIMEZONE
{% for elm in ClosingList %}
BEGIN:VEVENT
TRANSP:OPAQUE
SUMMARY:図書館休み
DTSTART;VALUE=DATE:{{elm.StartDate}}
DTEND;VALUE=DATE:{{elm.EndDate}}
END:VEVENT
{% endfor %}
")

(defn render-Calendar [events]
  (let [all-start-days (get-ClosingStartDays events)
        all-end-days (get-ClosingEndDays events)]
    (tmpl/render *CALENDAR-TEMPLATE* {:ClosingList (build-map all-start-days all-end-days)})))

(defn make-calendar-day [start-days end-days]
  (loop [result [] sd start-days ed end-days]
    (if (or (nil? (first sd)) (nil? (first ed))) result
        (recur (conj result (assoc {} :StartDate (first sd) :EndDate (first ed))) (rest sd) (rest ed)))))

(defn build-map [all-start-days all-end-days]
  (loop [result [] sd all-start-days ed all-end-days]
    (if (or (nil? (first sd)) (nil? (first ed))) result
        (recur (concat result (make-calendar-day (first sd) (first ed))) (rest sd) (rest ed)))))

(defn get-EventMonth [event-month-node]
   (assoc {} :month (first (:content (first (html/select event-month-node [:td.EventTitle :table :tr :td]))))
          :days (for [d (html/select event-month-node [:td.Closing])] (str/replace (str/replace (first (:content d)) "(" "") ")" ""))))

(defn gen-EventYearAttributes []
  (let [attr-base "table#dnn_ctr12401_Events_EventYear_EventCalendar"]
    (for [x (range 1 13)]  (keyword (str attr-base x)))))

(defn get-EventYearCalendar [url]
  (html/select (html/html-resource (java.net.URL. url)) [:div#EventYear_EventCalendars]))

(defn get-ClosingCalendar [event-year-calendar-node]
  (let [month-keys (gen-EventYearAttributes)]
    (for [k month-keys] (get-EventMonth (html/select event-year-calendar-node [k])))))

(defn format-yyyymmdd [year-month day]
  (str/join (map #(format "%02d" %) (conj (vec (map #(Integer/parseInt %) (str/split (str/replace year-month "月" "") #"年"))) day))))

(defn format-datetime [event-month-dic f]
  (for [month-day event-month-dic]
    (for [d (:days month-day)] (format-yyyymmdd (:month month-day) (f (Integer/parseInt d))))))

(defn get-ClosingStartDays [event-month-dic]
  (format-datetime event-month-dic clojure.core/identity))

(defn get-ClosingEndDays [event-month-dic]
  (format-datetime event-month-dic inc))

(defn make-iCal [url]
  (let [events (get-ClosingCalendar (get-EventYearCalendar url))]
    (render-Calendar events)))

(defn -main []
  (make-iCal "http://www.library.metro.tokyo.jp/guide/central_library/tabid/1410/Default.aspx"))
