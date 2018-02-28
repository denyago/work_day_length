[![Build Status](https://travis-ci.org/denyago/work_day_length.svg?branch=master)](https://travis-ci.org/denyago/work_day_length)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c6b641bcd8194d88a7f124d0c5e52d10)](https://www.codacy.com/app/denyago/work_day_length?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=denyago/work_day_length&amp;utm_campaign=Badge_Grade)

# Work Day Length
Calculates office hours based on RescueTime statistics

# Run

    sbt "runMain WorkDayLength.Cli.CliApp --start-date 2016-11-01 --end-date 2016-11-02"

## Roadmap

### MVP 
1. [X] Tests
2. [X] CLI app returns human-readable data
3. [X] Fix `--help` command
4. [X] Actually exit when finish (no need to `Ctrl+C`)
5. [X] Organize `Settings` package
6. [ ] Filter out too short activities
6. [ ] Specify "around time" for workday start and finish
7. [ ] Pass the API KEY via env variable/net.rc config/etc.
8. [ ] Sanity checks on real data and fix if needed

```
sbt "runMain WorkDayLength.Cli.CliApp --start-date 2016-11-01 --end-date 2016-11-02"
2016-11-01: Worked from 13:15 till 20:25:57 for 12 hours 18 minutes

sbt "runMain WorkDayLength.Cli.CliApp --start-date 2016-11-05 --end-date 2016-11-06"
2016-11-05: Worked from 18:15 till 14:41:54 for 6 hours 7 minutes
```

### Nice-to-have
1. [X] CI & Code analytics tools (`scalactic` for example)
2. [ ] CLI running script and `dist` build task
