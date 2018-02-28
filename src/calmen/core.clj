(ns calmen.core
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn get-event-month [lib-html-root]
  (html/select lib-html-root [:table.EventYear_EventCalendar]))

(def *lib-html-root* (html/select (fetch-url "http://www.library.metro.tokyo.jp/guide/central_library/tabid/1410/Default.aspx") [:table.EventMain]))
