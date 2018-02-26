(ns calmen.core
  (:require [clj-http.client :as htc]))

(defn get-html [url]
  (:body (htc/get url)))
