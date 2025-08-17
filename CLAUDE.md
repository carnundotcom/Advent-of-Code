# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Advent of Code solutions repository in Clojure, containing solutions from 2015-2024.
Each solution follows a consistent structure with both part 1 and part 2 implementations, tests using
`hyperfiddle.rcf`, and example data.

## Architecture & Structure

- **File Organization**: Solutions are organized as `src/y{YEAR}/d{DAY}.clj`
- **Data Files**: Input data stored in `data/y{YEAR}/d{DAY}.txt`
- **Utilities**: Common functions in `src/utils.clj` including:
  - `input` function that automatically loads data files based on namespace
  - Grid manipulation utilities (directions, positions, etc.)
  - MD5 hashing for cryptographic puzzles

## Common Development Commands

### Running Solutions
```clojure
;; In REPL - evaluate the comment block at bottom of each file
(part1 input)  ; Run part 1 with real input
(part2 input)  ; Run part 2 with real input
```

### Running Tests
```clojure
;; Tests are embedded using hyperfiddle.rcf
;; Run tests by evaluating the (tests ...) forms in each file
```

### Starting REPL
```bash
clj -M:dev  # Starts REPL with profiling JVM options
```

## Solution Patterns

Each day solution typically follows this structure:
1. Namespace declaration with `utils` and `hyperfiddle.rcf` requires
2. `dummy-input` with example data
3. `input` definition using `(u/input)`
4. Parsing functions to transform string input
5. `part1` and `part2` functions
6. `tests` block with example assertions
7. `comment` block for manual testing with real input

## Key Dependencies

- **hyperfiddle.rcf**: For inline testing
- **utils namespace**: Automatic input loading and common utilities
- **clojure.math.combinatorics**: For combinatorial problems
- **core.matrix**: For 2D grid operations
- **specter**: For complex data transformations
- **quil**: For visualization (some solutions)
- **clj-async-profiler**: Performance profiling (available in :dev)

## Development Notes

- Solutions prioritize readability and elegance over raw performance
- Many solutions include profiling and optimization iterations
- The `utils/input` function automatically determines input file based on calling namespace
- Grid-based problems often use the direction utilities in `utils.clj`
