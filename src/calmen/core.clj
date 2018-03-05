(ns calmen.core
  (:require [clojure.string :as str]
            [net.cgrand.enlive-html :as html]
            [selmer.parser :as tmpl]))

(def *CALENDAR-TEMPLATE* "
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
[% for elm in ClosingList %]
BEGIN:VEVENT
TRANSP:OPAQUE
SUMMARY:図書館休み
DTSTART;VALUE=DATE:{{elm.StartDate}}
DTEND;VALUE=DATE:{{elm.EndDate}}20180317
END:VEVENT
[% end for %]
")

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

(defn format-datetime [year-month day]
  (str/join (map #(format "%02d" %) (map #(Integer/parseInt %) (conj (str/split (str/replace year-month "月" "") #"年") day)))))

;(defn gen-ClosingList [event-month-dic]
;  (for [elm in event-month-dic))
  
(defn -main []
  (get-ClosingCalendar (get-EventYearCalendar "http://www.library.metro.tokyo.jp/guide/central_library/tabid/1410/Default.aspx")))
