#!/usr/bin/env bash

: ${PACT_BROKER_HOST:?"Need to pass PACT_BROKER_HOST variable"}
: ${PACT_BROKER_PORT:?"Need to pass PACT_BROKER_PORT variable"}

mvn verify -DPACT_BROKER_HOST=${PACT_BROKER_HOST} -DPACT_BROKER_PORT=${PACT_BROKER_PORT}