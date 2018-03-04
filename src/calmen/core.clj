(ns calmen.core
  (:require [clojure.string :as str]
            [net.cgrand.enlive-html :as html]
            [selmer.parser :as tmpl]))

(defn get-EventMonth [event-month-node]
  (list
   (assoc {} :month (first (:content (first (html/select event-month-node [:td.EventTitle :table :tr :td]))))
          :days (for [d (html/select event-month-node [:td.Closing])] (str/replace (str/replace (first (:content d)) "(" "") ")" "")))))

(defn gen-EventYearAttributes []
  (let [attr-base "table#dnn_ctr12401_Events_EventYear_EventCalendar"]
    (for [x (range 1 13)]  (keyword (str attr-base x)))))

(defn get-EventYearCalendar [url]
  (html/select (html/html-resource (java.net.URL. url)) [:div#EventYear_EventCalendars]))

(defn get-ClosingCalendar [event-year-calendar-node]
  (let [month-keys (gen-EventYearAttributes)]
    (for [k month-keys] (get-EventMonth (html/select event-year-calendar-node [k])))))

(defn -main []
  (get-ClosingCalendar (get-EventYearCalendar "http://www.library.metro.tokyo.jp/guide/central_library/tabid/1410/Default.aspx")))
