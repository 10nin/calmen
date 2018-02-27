(ns calmen.core
  (:require [clojure.string :as str]
            [pl.danieljanus.tagsoup :as tagsoup]))

(defn get-html [url]
  (tagsoup/parse url))
