echo "Running cljs tests."
npx shadow-cljs compile node-test
node target/shadow-node-test/node-tests.js