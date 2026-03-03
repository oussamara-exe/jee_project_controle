#!/bin/bash
set -e

# Variables MySQL (avec valeurs par défaut pour docker-compose)
MYSQL_HOST="${MYSQL_HOST:-mysql}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_DATABASE="${MYSQL_DATABASE:-gestion_rh}"
MYSQL_USER="${MYSQL_USER:-rh_user}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-rh_password_2024}"

# Répertoire WildFly
JBOSS_HOME=${JBOSS_HOME:-/opt/jboss/wildfly}
CONFIG_DIR="${JBOSS_HOME}/standalone/configuration"
CLI_SCRIPT="${CONFIG_DIR}/datasource-config.cli"

# Générer le script CLI avec les variables d'environnement
sed -e "s/__MYSQL_HOST__/${MYSQL_HOST}/g" \
    -e "s/__MYSQL_PORT__/${MYSQL_PORT}/g" \
    -e "s/__MYSQL_DATABASE__/${MYSQL_DATABASE}/g" \
    -e "s/__MYSQL_USER__/${MYSQL_USER}/g" \
    -e "s|__MYSQL_PASSWORD__|${MYSQL_PASSWORD}|g" \
    /opt/jboss/wildfly/docker/datasource.cli.template > "${CLI_SCRIPT}"

# Attendre que MySQL soit prêt (éviter les erreurs au démarrage)
echo "En attente de MySQL sur ${MYSQL_HOST}:${MYSQL_PORT}..."
while ! (echo >/dev/tcp/${MYSQL_HOST}/${MYSQL_PORT}) 2>/dev/null; do
  sleep 2
done
echo "MySQL est prêt."

# Démarrer WildFly en arrière-plan pour exécuter la config
echo "Configuration de la datasource..."
"${JBOSS_HOME}/bin/standalone.sh" -b 0.0.0.0 -bmanagement 0.0.0.0 &
WILDFLY_PID=$!

# Attendre que le serveur soit prêt
wait_for_wildfly() {
  local max_attempts=60
  local attempt=0
  while [ $attempt -lt $max_attempts ]; do
    if "${JBOSS_HOME}/bin/jboss-cli.sh" --connect --command=":read-attribute(name=server-state)" 2>/dev/null | grep -q "running"; then
      return 0
    fi
    attempt=$((attempt + 1))
    sleep 2
  done
  return 1
}

if wait_for_wildfly; then
  echo "WildFly démarré, ajout de la datasource..."
  if ! "${JBOSS_HOME}/bin/jboss-cli.sh" --connect --file="${CLI_SCRIPT}" 2>/dev/null; then
    echo "La datasource existe peut-être déjà (ignoré si redémarrage)."
  fi
  "${JBOSS_HOME}/bin/jboss-cli.sh" --connect --command=":shutdown"
  wait $WILDFLY_PID 2>/dev/null || true
fi

# Démarrer WildFly au premier plan
exec "${JBOSS_HOME}/bin/standalone.sh" -b 0.0.0.0 -bmanagement 0.0.0.0
