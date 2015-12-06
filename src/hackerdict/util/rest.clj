(ns hackerdict.util.rest)

(defn response
  [m]
  "have default status or headers but they can be overridden"
  (merge {:status 200 :headers {"Content-Type" "text/plain"}} m))
