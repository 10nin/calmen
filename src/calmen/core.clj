(ns calmen.core
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn get-EventMonth-node [event-month-node]
  (:content (first (html/select event-month-node [:td.EventTitle :table :tr :td]))))

(defn gen-EventYearAttributes []
  (let [attr-base "table#dnn_ctr12401_Events_EventYear_EventCalendar"]
    (for [x (range 1 13)]  (keyword (str attr-base x)))))

(defn get-EventYearCalendar [url]
  (html/select (fetch-url url) [:div#EventYear_EventCalendars]))

(defn get-EventMonths [event-year-calendar-node]
  (let [month-keys (gen-EventYearAttributes)]
    (for [k month-keys] (get-EventMonth-node (html/select event-year-calendar-node [k])))))

;"http://www.library.metro.tokyo.jp/guide/central_library/tabid/1410/Default.aspx"
