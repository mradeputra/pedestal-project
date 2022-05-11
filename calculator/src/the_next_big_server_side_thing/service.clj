(ns the-next-big-server-side-thing.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [hiccup.page :as hiccup]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn home-page
  [request]
  (ring-resp/response 
   (hiccup/html5
    [:html
     [:head
      [:script "function dis(val)\n{\ndocument.getElementById(\"result\").value+=val\n}\nfunction solve()\n{\nlet x = document.getElementById(\"result\").value\nlet y = eval(x)\ndocument.getElementById(\"result\").value = y\n}\nfunction clr()\n{\ndocument.getElementById(\"result\").value = \"\"\n}
   "]
  ;;     [:style ".title{\nmargin-bottom: 10px;\ntext-align:center;\nwidth: 210px;\ncolor:green;\nborder: solid black 2px;\n}\ninput
  ;;  [type=\"button\"]\n{\nbackground-color:orange;\ncolor: black;\nborder: solid black 2px;\nwidth:100%\n}\ninput
  ;;  [type=\"text\"]\n{\nbackground-color:white;\nborder: solid black 2px;\nwidth:100%\n}
  ;;  "]
      ]
     [:body
      [:div {:class "title"}
       "Calculator"]
      [:table {:border "1"}
       [:tbody
        [:tr
         [:td {:colspan "3"}
          [:input {:type "text", :id "result"}]]
         [:td
          [:input {:type "button", :value "c", :onclick "clr()"}]]]
        [:tr
         [:td
          [:input {:type "button", :value "1", :onclick "dis('1')"}]]
         [:td
          [:input {:type "button", :value "2", :onclick "dis('2')"}]]
         [:td
          [:input {:type "button", :value "3", :onclick "dis('3')"}]]
         [:td
          [:input {:type "button", :value "/", :onclick "dis('/')"}]]]
        [:tr
         [:td
          [:input {:type "button", :value "4", :onclick "dis('4')"}]]
         [:td
          [:input {:type "button", :value "5", :onclick "dis('5')"}]]
         [:td
          [:input {:type "button", :value "6", :onclick "dis('6')"}]]
         [:td
          [:input {:type "button", :value "-", :onclick "dis('-')"}]]]
        [:tr
         [:td
          [:input {:type "button", :value "7", :onclick "dis('7')"}]]
         [:td
          [:input {:type "button", :value "8", :onclick "dis('8')"}]]
         [:td
          [:input {:type "button", :value "9", :onclick "dis('9')"}]]
         [:td
          [:input {:type "button", :value "+", :onclick "dis('+')"}]]]
        [:tr
         [:td
          [:input {:type "button", :value ".", :onclick "dis('.')"}]]
         [:td
          [:input {:type "button", :value "0", :onclick "dis('0')"}]]
         [:td
          [:input {:type "button", :value "=", :onclick "solve()"}]]
         [:td
          [:input {:type "button", :value "*", :onclick "dis('*')"}]]]]]]]
    )))

;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/about" :get (conj common-interceptors `about-page)]})

;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) http/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
;(def routes
;  `[[["/" {:get home-page}
;      ^:interceptors [(body-params/body-params) http/html-body]
;      ["/about" {:get about-page}]]]])


;; Consumed by the-next-big-server-side-thing.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
                                                                      ;;  :script-src "'unsafe-inline' 'unsafe-hashes' 'unsafe-eval' 'strict-dynamic' https: http:"
                                                                       :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false
                                        ;; Alternatively, You can specify you're own Jetty HTTPConfiguration
                                        ;; via the `:io.pedestal.http.jetty/http-configuration` container option.
                                        ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
                                        }})
