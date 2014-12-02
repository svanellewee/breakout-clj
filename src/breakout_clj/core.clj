(ns breakout-clj.core
  (:import (org.lwjgl LWJGLException)
           (org.lwjgl.input Keyboard Mouse)
           (org.lwjgl.opengl Display DisplayMode GL11))
  (:gen-class))

;; (defmacro with-display
;;   [ setup-display main-loop ]
;;   `(try
;;      (Display/setDisplayMode (DisplayMode. 800 600))
;;      (Display/create)
;;      (quote ~(setup-display))
;;      ;;~((main-loop))
;;      (catch LWJGLException e#
;;        (str "Caught Exception" (.getMessage e#)) )))

(defn with-display
  [ fn-setup-display fn-main-loop ]
  (try
     (Display/setDisplayMode (DisplayMode. 800 600))
     (Display/create)
     (fn-setup-display)
     (fn-main-loop)
     (catch LWJGLException e#
       (str "Caught Exception" (.getMessage e#)) )))

(defn setup-display
  []
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho 0 800 0 600 1 -1)
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

;; (defn poll-keyboard-input 
;;   ([] (poll-keyboard-input []))
;;   ([ states ]
;;    (if-not (Keyboard/next)
;;      states
;;      (let [ key (Keyboard/getEventKey)
;;            pressed-released (if (Keyboard/getEventKeyState) "pressed" "released") 
;;            key-state (str pressed-released (condp = key
;;                                              Keyboard/KEY_A  "A"
;;                                              Keyboard/KEY_S  "S"
;;                                              (str "Default" key )))]
;;      (recur (conj states key-state) )))))


;; (defn main-loop
;;   []
;;   (when-not (Display/isCloseRequested)
;;     (let [value (poll-keyboard-input)]
;;       (if (< 0 (count value) )
;;         (println "---->" value)))
;;     (Display/update)
;;     (recur)) )


(defn poll-keyboard-input 
  ([ state ]
   (if-not (Keyboard/next)
     state
     (let [ key (Keyboard/getEventKey)
           pressed-released (if (Keyboard/getEventKeyState) 
                              :pressed
                              :released) 
           new-state  (if  (or 
                            (= key Keyboard/KEY_A)  
                            (= key Keyboard/KEY_S)) 
                        (assoc state :control { :pressed-released pressed-released :movement (condp = key
                                                                                               Keyboard/KEY_A  :up
                                                                                               Keyboard/KEY_S  :down
                                                                                               :ignore)})
                        {})
                       
           ]
     (recur new-state )))))

(defn make-main-loop 
  [  callback ]
  (letfn [ (main-loop 
             ([] (main-loop {}))
             ( [ state ] 
               (when-not (Display/isCloseRequested)
                 (let [ new-state (callback state) ]
                   (when-not (empty? new-state) 
                     (println ">-->" new-state))
                   (Display/update)
                   (recur new-state)))))
           ]
    main-loop))

;; (defn main-loop
;;   ([] (main-loop {}))
;;   ([ state ] 
;;    (when-not (Display/isCloseRequested)

;;      (Display/update)
;;      (recur)) )
;; )
   
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (let [ main-loop (make-main-loop poll-keyboard-input)]
    (println main-loop)
    (with-display
      setup-display main-loop))
  )
