(ns hackerdict.mock-data)



(def mock-side-bar-data
  [
   {:subject-text "clojure" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "compojure" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "ring" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "selmer" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "luminus" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "lisp" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "rich hickey" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "om" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "om/next" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "hy" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "pixie" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "david nolen" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "significant people in clojure community" :n-entries-today 5 :n-entries-total 10}
   {:subject-text "validation libraries" :n-entries-today 5 :n-entries-total 10}
   ])


(def mock-user-data
  {:username "ustunozgur"
   :github-username "ustunozgur"
   :first-name "Ustun"
   :last-name "Ozgur"
   :email "ustun@ustunozgur.com"})


(def mock-main-feed-data
  {:subject "clojure"
   :entries [
             {:text "clojure is a lisp" :creator "ustunozgur" :date-created #inst "2015-12-05"}
             {:text "clojure runs on the jvm" :creator "ustunozgur" :date-created #inst "2015-12-05"}
             {:text "clojure is fun" :creator "ustunozgur" :date-created #inst "2015-12-05"}
             ]
   :meta {:page 1 :date-latest-entry #inst "2015-12-05"}
   })


(def mock-main-page-data
  {:sidebar mock-side-bar-data
   :user mock-user-data
   :main mock-main-feed-data})
