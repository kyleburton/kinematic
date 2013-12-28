(ns kinematic.router
  (:require
   [clojure.string :as string])
  (:use
   [kinematic.util :only [substr]]))


(comment

  (defapp :my-app
    :mount-point "/my-app"
    :before      [#_"seq of kinematic handlers"]
    :after       [#_"seq of kinematic handlers"])


  (api/register :my-app ["/index"]
                :before [#_"seq of kinematic handlers"]
                :after  [#_"seq of kinematic handlers"])


  )

(defn pattern-matches-uri? [^String mount-point ^String pattern ^String uri]
  (let [uri             (substr uri (count mount-point))
        uri             (if (.startsWith uri "/")
                          (substr uri 1)
                          uri)
        pattern-parts   (.split pattern "/")
        uri-parts       (.split uri     "/")
        contains-splat? (not= -1 (.indexOf pattern "*"))]
    (if (and
         (not contains-splat?)
         (not (= (count pattern-parts)
                 (count uri-parts))))
      {:matched      false
       :route-params {}
       ;; :reason (format "Count mismatch: %s vs %s"
       ;;                 (count pattern-parts)
       ;;                 (count uri-parts))
       }
      (loop [[pp & pattern-parts] pattern-parts
             [up & uri-parts]     uri-parts
             route-params         {}]
        ;; did we run out of uri-parts and pattern-parts?
        (cond
          (and (not pp)
               (not up))
          {:matched      true
           :route-params route-params}

          ;; if a parameter, it's a match
          (.startsWith pp ":")
          (recur pattern-parts uri-parts (assoc route-params (keyword (substr pp 1))
                                                up))

          (.startsWith pp "*")
          {:matched true
           :route-params (assoc route-params
                           (keyword (substr pp 1))
                           (string/join "/" (cons up uri-parts)))}

          (= pp up)
          (recur pattern-parts uri-parts route-params)

          :otherwise
          {:matched      false
           :route-params route-params
           ;; :reason       "Fell through."
           })))))

(comment

  (pattern-matches-uri? "/my-app" "users/:id/*rest"
                                      "/my-app/users/1234/some/more/stuff")

)