(ns wsir.core
  (:require
    [hickory.core :as hickory]
    [hickory.select :as selector]
    [clj-http.client :as client]
    [clojure.string :as string]))

(defn http-get [link]
  (-> link
      client/get
      :body
      hickory/parse
      hickory/as-hickory))

(def- clojar-query (partial str clojar))
(def- clojar "https://clojars.org/search?q=")
(defn- clojar-result-processing [site-result]
  (->> (selector/select (selector/child
                          (selector/class :result)
                          (selector/tag :div)
                          (selector/tag :div)) site-result)
       (mapcat :content)
       (map #(hash-map :content (:content %) :href (:href (:attrs %))))
       (filter #( :content %))))

(def clojar-search (comp clojar-result-processing http-get clojar-query))

