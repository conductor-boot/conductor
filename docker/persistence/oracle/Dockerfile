# Dockerfile
FROM conductorboot/oracle:19.3.0-ee-slim

ENV ORACLE_SID CONDUCTR
ENV ORACLE_PDB CONDUCTOR
ENV ORACLE_PWD Conductor@1234
ENV ORACLE_EDITION enterprise

COPY 01_environment_details.sh /opt/oracle/scripts/setup/01_environment_details.sh
COPY 02_CREATE_SCHEMA_AND_USER.sql //opt/oracle/scripts/setup/02_CREATE_SCHEMA_AND_USER.sql
