(ns breakout-clj.core
  (:import (org.lwjgl LWJGLException)
           (org.lwjgl.input Keyboard Mouse)
           (org.lwjgl.opengl Display DisplayMode GL11))
  (:gen-class))


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
 ;;   ([ state ]
 ;;    (if-not (Keyboard/next) 
 ;;      state
 ;;      (let [ 
 ;;            ;;current-key-state (fn [] (let [ key (Keyboard/getEventKey)  pressed (Keyboard/getEventKeyState) ] (println "NORM checking state") {:key key :pressed? pressed }))
 ;;            current-key-state (fn [] (let [ 
 ;;                                            key (Keyboard/getEventKey)  

 ;;                                            pressed (if (Keyboard/getEventKeyState) 
 ;;                                                      :pressed
 ;;                                                      :released)
 
 ;;                                            movement (condp = key
 ;;                                                       Keyboard/KEY_A  :up
 ;;                                                       Keyboard/KEY_S  :down
 ;;                                                       :ignore)

 ;;                                            pressed (Keyboard/getEventKeyState)
                                       
                                       
 ;;                                            retval {:movment movement :pressed-released pressed } ]
 ;;                                       (if (not= movement :ignore) 
 ;;                                         retval) ))
 ;;            ]
        
 ;;        (let [ control-state (current-key-state) ]
 ;;          (println "..!!!" control-state)
 ;;          (if-not control-state
 ;;            state
 ;;            (do 
 ;;              (println "Do something with " control-state)
 ;;              state)))))))


 (defn poll-keyboard-input 
   ([ state ]
    (if-not (Keyboard/next) 
      state
      (let [ 
            ;;current-key-state (fn [] (let [ key (Keyboard/getEventKey)  pressed (Keyboard/getEventKeyState) ] (println "NORM checking state") {:key key :pressed? pressed }))
            current-key-state (fn [] (let [ 
                                            key (Keyboard/getEventKey)  

                                            pressed (if (Keyboard/getEventKeyState) 
                                                      :pressed
                                                      :released)
 
                                            movement (condp = key
                                                       Keyboard/KEY_A  :up
                                                       Keyboard/KEY_S  :down
                                                       :ignore)

                                            pressed (Keyboard/getEventKeyState)
                                       
                                       
                                            retval {:movement movement :pressed-released pressed } ]
                                       (if (not= movement :ignore) 
                                         retval) ))
            ]
        
        (let [ control-state (current-key-state) ]
          (println "..!!!" control-state state)
          (if-not control-state
            state
            (let [ new-state (assoc state :control control-state) ] 
              (println "Do someethi<<<" new-state)
              new-state)))))))


 (defn orig-poll-keyboard-input 
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
                        state)
                       
            ]
      (recur new-state )))))



(defn draw-paddle 
  [ x y ]
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))
  
  (GL11/glColor3f 0.5 0.5 1.0)
  
  (GL11/glBegin GL11/GL_QUADS)
   (GL11/glVertex2f x y)
   (GL11/glVertex2f (+ x 15) y)
   (GL11/glVertex2f (+ x 15) (+ y 80) )
   (GL11/glVertex2f x (+ y 80))
  (GL11/glEnd)
  )


(defn old-update-players
  [ state ]
  (if-let [control (:control state) ]
    (when-let [ movement (:movement control) ] 
      (println "---" state, control)

      (let [ paddle (:paddle state)
             old-x (-> state :paddle :x)
             old-y (-> state :paddle :y) 
             new-paddle (condp = movement 
                          :up   (assoc paddle :x old-x :y (+ old-y 5))
                          :down (assoc paddle :x old-x :y (- old-y 5)) 
                          paddle) ]
        { :paddle new-paddle }))
    state))

(defn update-players 
  [ state ] 
  (if-let [ control (:control state)  ]
    (do 
      ;;(println "control = " state)
      (if-let [ movement (:movement control) ]
        (let [ paddle (:paddle state)
               old-x (-> state :paddle :x)
               old-y (-> state :paddle :y) 
               new-paddle (condp = movement 
                            :up   (assoc paddle :x old-x :y (+ old-y 5))
                            :down (assoc paddle :x old-x :y (- old-y 5)) 
                            paddle) ]
          ;;{:paddle new-paddle}  ;; don't keep the direction data...
          (assoc state :paddle new-paddle)  ;; keep the direction/movement data
          ;;(println "movement =" movement)
          ;;state
          )
        state))
    state))

(defn apply-callbacks 
  [ state callbacks ]
  (if (empty? callbacks) state
    (let [ callback (first callbacks) 
           the-other-callbacks (rest callbacks)
           new-state (callback state)  ]
      (recur new-state the-other-callbacks))))

(defn make-main-loop 
  [ & callbacks ]
  (letfn [ (main-loop 
             ([] (main-loop {:paddle {:x 10 :y 10} }))
             ( [ state ] 
               (when-not (Display/isCloseRequested)
                 (let [ new-state (apply-callbacks state callbacks) ]
                   (when-not (empty? new-state) 
                     (draw-paddle (-> state :paddle :x) (-> state :paddle :y))

                     ;;(println ">-->" new-state)
                     )

                   (Display/update)
                   (recur new-state)))))
           ]
    main-loop))

   
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (let [ main-loop (make-main-loop poll-keyboard-input update-players)]
    (println main-loop)
    (with-display
      setup-display main-loop))
  )
