(ns DiningPhilosophers.core-test
  (:use clojure.test
        DiningPhilosophers.core))

; there are 5 philos.
(deftest table-tests
	(is (= [0 4] (fork-ids 0)))
	(is (= [0 1] (fork-ids 1))))


(deftest forks-test
	(is (= [0 0] 
		(get-forks 0 [0 2 2 nil 0])))
	(is (= [1 1] 
		(get-forks 1 [1 1 nil nil nil])))
	(is (= [1 nil] 
		(get-forks 2 [1 1 nil nil nil])))
	(is (= [1 nil] 
		(map deref (get-forks 2 
			[(ref 1) (ref 1) (ref nil) (ref nil) (ref nil)])))))

(deftest grab-forks-test
	(is (= [0 2 2 nil 0]
		(map deref
			(dosync
				(grab-forks 0 
					(map ref [nil 2 2 nil nil]))))))
	(is (= [1 1 2 nil nil]
		(map deref
			(dosync
				(grab-forks 1 
					(map ref [nil 2 2 nil nil])))))))

(deftest drop-forks-test
	(is (= [nil nil nil nil nil]
		(map deref
			(dosync
				(drop-forks 2 
					(map ref [nil 2 2 nil nil]))))))
	(is (= [nil 2 2 nil nil]
		(map deref
			(dosync
				(drop-forks 0 
					(map ref [0 2 2 nil 0])))))))

(deftest eat-logging 
	(is 
		(= [2 :eating 200]
			(let [log (agent [])]
				(eat 2 log 200)
				(rest (first @log))))))

(deftest think-test
	(is 
		(= [2 :thinking 150]
			(let [log (agent [])]
				(think 2 log 150)
				(rest (first @log))))))

(deftest philosopher-test
	(is
		(= 20
			(let [log (agent [])]
				(philosopher 1 10 log (map ref (repeat 5 nil)))
				(Thread/sleep 1000)
				(count @log)))))

(deftest dinner-test
	(is
		(= 50
			(let [log (agent [])]
				(dinner 5 5 log)
				(Thread/sleep 1000)
				(count @log)))))
