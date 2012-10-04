(ns DiningPhilosophers.core
	(:use [clojure.tools.cli :only [cli]]
		  [clojure.data.json :only [json-str]])
	(:gen-class))

(defn now [] (System/currentTimeMillis))

(defn fork-ids [philo-id]
	(sort [philo-id (mod (dec philo-id) 5)]))

(defn get-forks [philo-id table] 
	(map (fn [pos] (nth table pos)) (fork-ids philo-id)))

(defn grab-forks [philo-id table]
	(doseq [fork (get-forks philo-id table)]
		(ref-set fork philo-id))
	table)

(defn drop-forks [philo-id table]
	(let [cur-forks (get-forks philo-id table)]
		(when (= [philo-id philo-id] (map deref cur-forks))
			(doseq [fork cur-forks]
				(ref-set fork nil)))
		table))

(defn action [action philo-id log duration]

	(send-off log conj [(now) philo-id action duration])
	(Thread/sleep duration))

(def eat (partial action :eating))
(def think (partial action :thinking))

(defn philosopher [philo-id numtimes log table]
	(dotimes [_  numtimes]
		(think philo-id log (rand-int 200))
		(dosync 
			(grab-forks philo-id table)
			(eat philo-id log (rand-int 200))
			(drop-forks philo-id table))))

(defn dinner [philocount numtimes log]
	(let [table (map ref (repeat philocount nil))]
		(doall 
			(pmap (fn [philo-id] 
				(philosopher philo-id numtimes log table)) 
				(range philocount)))))

(defn run 
  "Run the simulation and print the output"
  [philocount times]
	(let [log (agent [])]
		  (println (str "Table of " philocount " philosophers, each eating " times " times" ))
		  (dinner philocount times log)
		  (Thread/sleep 1000)
		  (println (json-str @log)))
	(shutdown-agents))

(defn -main [& args]
	(let [[opts args banner]
		 	(cli args
			     ["-s" "--philocount" "number of philosophers/forks" :parse-fn #(Integer. %)] 
			     ["-n" "--times" "number of iterations each philosopher eats" :parse-fn #(Integer. %)])]
		(if
	        (and (:philocount opts) (:times opts))
      		(run (:philocount opts) (:times opts))
      		(println banner))))
