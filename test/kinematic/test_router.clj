(ns kinematic.test-router
  (:require
   [kinematic.router :as router]
   [kinematic.dsl    :as kdsl])
  (:use
   clojure.test))

(comment
  (do
    (kdsl/defweb :kinematic-test-1
      :mount-point   "/my-app"
      :app-ns-prefix :kinematic.test-router
      :not-found     (fn [ctx]
                       {:status 404
                        :body (str "NotFound: " (-> ctx :request :uri))}))
    (kdsl/defapi :kinematic-test-1 ["index"])
    (kdsl/api-get
     (def ctx request)
     {:status 200
      :body "index"}))

  (kinematic.core/route-info :kinematic-test-1)
  (kinematic.dsl/app-route-info :kinematic-test-1)

  (require 'org.httpkit.server)
  (defonce stop-server (atom nil))
  (reset! stop-server
          (org.httpkit.server/run-server
           (kdsl/dyn-handler :kinematic-test-1)
           {:port 8888}))
  (@stop-server)

  )

(deftest test-uri-matcher
  (is (= {:matched false :route-params {}} (router/pattern-matches-uri? "/my-app" "index" "/")))
  (is (= {:matched false :route-params {}} (router/pattern-matches-uri? "/my-app" "index" "/index")))
  (is (= {:matched true  :route-params {}} (router/pattern-matches-uri? "/my-app" "index" "/my-app/index")))
  (is (= {:matched false :route-params {}} (router/pattern-matches-uri? "/my-app" "index" "/my-app/index/banana")))
  (is (= {:matched true  :route-params {:id "1234"}}
         (router/pattern-matches-uri? "/my-app" "users/:id" "/my-app/users/1234")))
  (is (= {:matched true :route-params {:rest "some/more/stuff"
                                       :id   "1234"}}
         (router/pattern-matches-uri? "/my-app" "users/:id/*rest"
                                      "/my-app/users/1234/some/more/stuff"))))

(comment

  (kinematic.core/make-application
   :my-app {})

  (kinematic.core/register-route :my-app ["/index"] {"GET" (fn [& args])})

  (kinematic.core/routing-table :my-app)

  (run-tests)
  )