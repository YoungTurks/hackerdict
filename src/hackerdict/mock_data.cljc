(ns hackerdict.mock-data)



(def mock-side-bar-data
  [
   {:text "clojure" :n-entries-today 5 :n-entries-total 10}
   {:text "compojure" :n-entries-today 5 :n-entries-total 10}
   {:text "ring" :n-entries-today 5 :n-entries-total 10}
   {:text "selmer" :n-entries-today 5 :n-entries-total 10}
   {:text "luminus" :n-entries-today 5 :n-entries-total 10}
   {:text "lisp" :n-entries-today 5 :n-entries-total 10}
   {:text "rich hickey" :n-entries-today 5 :n-entries-total 10}
   {:text "om" :n-entries-today 5 :n-entries-total 10}
   {:text "om/next" :n-entries-today 5 :n-entries-total 10}
   {:text "hy" :n-entries-today 5 :n-entries-total 10}
   {:text "pixie" :n-entries-today 5 :n-entries-total 10}
   {:text "david nolen" :n-entries-today 5 :n-entries-total 10}
   {:text "significant people in clojure community" :n-entries-today 5 :n-entries-total 10}
   {:text "validation libraries" :n-entries-today 5 :n-entries-total 10}
   ])


(def mock-user-data
  {:username "ustunozgur"
   :github-username "ustunozgur"
   :name "Ustun Ozgur"
   :email "ustun@ustunozgur.com"})


(def mock-main-feed-data
  {:subject "clojure"
   :entries [
             {:text "clojure is a lisp" :username "ustunozgur" :date-added #inst "2015-12-05"}
             {:text "clojure runs on the jvm" :username "ustunozgur" :date-added #inst "2015-12-05"}
             {:text "clojure is fun" :username "ustunozgur" :date-added #inst "2015-12-05"}
             ]
   :meta {:page 1 :date-latest-entry #inst "2015-12-05"}
   })


(def mock-main-page-data
  {:sidebar mock-side-bar-data
   :user mock-user-data
   :main mock-main-feed-data})
