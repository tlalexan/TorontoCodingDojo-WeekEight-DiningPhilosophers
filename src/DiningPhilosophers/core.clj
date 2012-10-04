(ns DiningPhilosophers.core)

(defn now [] (.getTime (new java.util.Date)))

(defn fork-ids [philo-id]
	(sort [philo-id (mod (dec philo-id) 5)]))

(defn get-forks [philo-id table] 
	[(nth table (first (fork-ids philo-id))),
	 (nth table (second (fork-ids philo-id)))] )

(defn grab-forks [philo-id table]
	(let [cur-forks (get-forks philo-id table)]
		(if (= [nil nil] (map deref cur-forks))
			(doseq [fork cur-forks]
				(ref-set fork philo-id))
			(throw (Exception. (str "Couldn't get forks"))))
		table))

(defn drop-forks [philo-id table]
	(let [cur-forks (get-forks philo-id table)]
		(when (= [philo-id philo-id] (map deref cur-forks))
			(doseq [fork cur-forks]
				(ref-set fork nil)))
		table))

(defn eat [philo-id log duration]
	(send log #(conj % (str "Philosopher " philo-id " eats for " duration " at " (now))))
	(Thread/sleep duration))

(defn think [philo-id log duration]
	(send log #(conj % (str "Philosopher " philo-id " thinks for " duration " at " (now))))
	(Thread/sleep duration))

(defn one-philo-cycle [philo-id counter log table]
	(dotimes [_  counter]
		(think philo-id log (rand-int 200))
		(dosync 
			(grab-forks philo-id table)
			(eat philo-id log (rand-int 200))
			(drop-forks philo-id table))))