(ns hackerdict.util)

(defn clean-nil-vals
  "Cleans nil values from a map"
  [original-map]
  (into {} (filter second original-map)))
