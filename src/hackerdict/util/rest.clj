(ns hackerdict.util.rest)

(defn response
  [{:keys [status headers body]}]
  {:status  (or status 200)
   :headers (or headers {"Content-Type" "text/plain"})
   :body    body})
