(ns my-module
  (:require [com.wsscode.pathom.core :as p]
            [com.wsscode.pathom.connect :as pc]))

(def people-table
  {1 {:person/id 1 :person/name "Sally" :person/age 32}
   2 {:person/id 2 :person/name "Joe" :person/age 22}
   3 {:person/id 3 :person/name "Fred" :person/age 11}
   4 {:person/id 4 :person/name "Bobby" :person/age 55}})

(def list-table
  {:friends {:list/id     :friends
             :list/label  "Friends"
             :list/people [{:person/id 1} {:person/id 2}]}
   :enemies {:list/id     :enemies
             :list/label  "Enemies"
             :list/people [{:person/id 4} {:person/id 3}]}})

;; Given :person/id, this can generate the details of a person
(pc/defresolver person-resolver [env {:person/keys [id]}]
  {::pc/input  #{:person/id}
   ::pc/output [:person/name :person/age]}
  (get people-table id))

(def registry [person-resolver])

(def pathom-parser
  (p/parser {::p/env                  {::p/reader [p/map-reader
                                                   pc/reader2
                                                   pc/open-ident-reader
                                                   p/env-placeholder-reader]}
             ::p/placeholder-prefixes #{">"}
             ::p/mutate               pc/mutate
             ::p/plugins              [(pc/connect-plugin {::pc/register registry})
                                       p/error-handler-plugin
                                       p/trace-plugin]}))

(js/console.log "RES" (pathom-parser {} [{[:person/id 2] [:person/name :person/age]}]))
