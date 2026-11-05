find . -iname '*.clj*' | entr sh -c "./scripts/format.sh; ./scripts/example.sh"
