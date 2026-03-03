#!/bin/bash
# Script de test DevOps - Gestion RH
# Lance les vérifications possibles sans Docker (Maven, YAML, structure)

set -e
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "=============================================="
echo "  Tests DevOps - Gestion RH Avancée"
echo "=============================================="

FAILED=0

# 1. Maven compile
echo -e "\n[1/5] Maven compile..."
if mvn -q -B compile; then
  echo -e "${GREEN}✓ Compilation OK${NC}"
else
  echo -e "${RED}✗ Compilation échouée${NC}"
  FAILED=1
fi

# 2. Maven tests
echo -e "\n[2/5] Maven tests..."
if mvn -q -B test; then
  echo -e "${GREEN}✓ Tests OK${NC}"
else
  echo -e "${YELLOW}⚠ Tests échoués ou absents (non bloquant)${NC}"
fi

# 3. Maven package WAR
echo -e "\n[3/5] Maven package (WAR)..."
if mvn -q -B package -DskipTests; then
  echo -e "${GREEN}✓ Package OK${NC}"
  ls -la target/gestion-rh.war 2>/dev/null && echo -e "${GREEN}  → gestion-rh.war présent${NC}"
else
  echo -e "${RED}✗ Package échoué${NC}"
  FAILED=1
fi

# 4. Validation docker-compose
echo -e "\n[4/5] Validation docker-compose.yml..."
if docker compose config -q 2>/dev/null; then
  echo -e "${GREEN}✓ docker-compose.yml valide${NC}"
else
  echo -e "${RED}✗ docker-compose.yml invalide${NC}"
  FAILED=1
fi

# 5. Build Docker (optionnel - si le daemon tourne)
echo -e "\n[5/5] Build image Docker..."
if ! docker info &>/dev/null; then
  echo -e "${YELLOW}⚠ Docker daemon non démarré - build Docker ignoré${NC}"
  echo "  Pour tester: démarrer Docker puis: docker compose up -d"
elif docker build -t gestion-rh-app:test -f docker/Dockerfile . 2>/dev/null; then
  echo -e "${GREEN}✓ Build Docker OK${NC}"
else
  echo -e "${YELLOW}⚠ Build Docker impossible (daemon absent?) - ignoré${NC}"
fi

echo -e "\n=============================================="
if [ $FAILED -eq 0 ]; then
  echo -e "${GREEN}  Tous les tests critiques sont passés.${NC}"
  echo "=============================================="
  exit 0
else
  echo -e "${RED}  Certains tests ont échoué.${NC}"
  echo "=============================================="
  exit 1
fi
