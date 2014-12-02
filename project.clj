(defproject breakout-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.lwjgl.lwjgl/lwjgl "2.9.1"]
                 [org.lwjgl.lwjgl/lwjgl-platform "2.9.1"
                  :classifier "natives-osx"
                  ;; LWJGL stores natives in the root of the jar; this
                  ;; :native-prefix will extract them.
                  :native-prefix ""]] 
  :main ^:skip-aot breakout-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
