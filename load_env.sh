#!/bin/bash

# Script pentru încărcarea variabilelor de mediu în sesiunea curentă
# Folosire: source load_env.sh [fisier_env]

# Funcție pentru încărcarea unui fișier .env
load_env_file() {
    local env_file="$1"

    if [[ ! -f "$env_file" ]]; then
        echo "❌ Fișierul $env_file nu există!"
        return 1
    fi

    echo "📂 Încarcă variabile din: $env_file"

    # Citește fișierul linie cu linie
    while IFS= read -r line || [[ -n "$line" ]]; do
        # Ignoră liniile goale și comentariile
        if [[ -z "$line" || "$line" =~ ^[[:space:]]*# ]]; then
            continue
        fi

        # Verifică dacă linia conține format KEY=VALUE
        if [[ "$line" =~ ^[[:space:]]*([A-Za-z_][A-Za-z0-9_]*)=(.*)$ ]]; then
            local key="${BASH_REMATCH[1]}"
            local value="${BASH_REMATCH[2]}"

            # Îndepărtează ghilimelele din jur dacă există
            if [[ "$value" =~ ^\"(.*)\"$ ]] || [[ "$value" =~ ^\'(.*)\'$ ]]; then
                value="${BASH_REMATCH[1]}"
            fi

            # Exportă variabila
            export "$key=$value"
            echo "✅ $key=$value"
        else
            echo "⚠️  Linie invalidă ignorată: $line"
        fi
    done < "$env_file"
}

# Funcție pentru afișarea variabilelor încărcate
show_loaded_vars() {
    echo ""
    echo "🔍 Variabile de mediu încărcate:"
    echo "================================"
    env | grep -E "^(DB_|API_|APP_|JWT_|REDIS_|MONGO_)" | sort
}

# Funcție pentru salvarea variabilelor curente
save_current_env() {
    local output_file="${1:-.env.backup}"
    echo "💾 Salvează variabilele curente în: $output_file"
    env | grep -E "^(DB_|API_|APP_|JWT_|REDIS_|MONGO_)" > "$output_file"
    echo "✅ Salvat în $output_file"
}

# Funcție pentru ștergerea variabilelor
clear_env_vars() {
    echo "🧹 Șterge variabilele de mediu..."

    # Lista de prefixuri comune
    local prefixes=("DB_" "API_" "APP_" "JWT_" "REDIS_" "MONGO_" "AWS_" "GOOGLE_")

    for prefix in "${prefixes[@]}"; do
        for var in $(env | grep "^${prefix}" | cut -d= -f1); do
            unset "$var"
            echo "🗑️  Șters: $var"
        done
    done
}

# Funcție pentru ajutor
show_help() {
    cat << EOF
🔧 Script pentru gestionarea variabilelor de mediu

Folosire:
  source load_env.sh [opțiuni] [fisier]

Opțiuni:
  -h, --help          Afișează acest ajutor
  -s, --show          Afișează variabilele încărcate
  -c, --clear         Șterge variabilele de mediu
  -b, --backup [file] Salvează variabilele curente

Exemple:
  source load_env.sh                    # Încarcă din .env
  source load_env.sh .env.local         # Încarcă din fișier specific
  source load_env.sh --show             # Afișează variabilele
  source load_env.sh --clear            # Șterge variabilele
  source load_env.sh --backup           # Salvează în .env.backup

Notă: Scriptul trebuie rulat cu 'source' pentru a afecta sesiunea curentă!
EOF
}

# Procesarea argumentelor
main() {
    local env_file=".env"
    local show_vars=false
    local clear_vars=false
    local backup_file=""

    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                return 0
                ;;
            -s|--show)
                show_vars=true
                shift
                ;;
            -c|--clear)
                clear_vars=true
                shift
                ;;
            -b|--backup)
                backup_file="${2:-.env.backup}"
                shift 2
                ;;
            -*)
                echo "❌ Opțiune necunoscută: $1"
                show_help
                return 1
                ;;
            *)
                env_file="$1"
                shift
                ;;
        esac
    done

    # Execută acțiunile
    if [[ "$clear_vars" == true ]]; then
        clear_env_vars
    fi

    if [[ -n "$backup_file" ]]; then
        save_current_env "$backup_file"
    fi

    if [[ -f "$env_file" ]]; then
        load_env_file "$env_file"
    elif [[ "$env_file" != ".env" ]]; then
        echo "❌ Fișierul $env_file nu există!"
        return 1
    fi

    if [[ "$show_vars" == true ]]; then
        show_loaded_vars
    fi

    echo ""
    echo "🎉 Gata! Variabilele sunt disponibile în sesiunea curentă."
}

# Verifică dacă scriptul este rulat cu source
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    echo "⚠️  ATENȚIE: Rulează scriptul cu 'source' pentru a încărca variabilele în sesiunea curentă:"
    echo "   source ${BASH_SOURCE[0]} [opțiuni]"
    exit 1
fi

# Rulează funcția principală
main "$@"