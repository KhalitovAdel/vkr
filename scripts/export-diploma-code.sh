#!/usr/bin/env bash
# Выгрузка фрагментов исходного кода в один UTF-8 .txt для приложения к ВКР.
# Настройка объёма: переменные окружения MAX_TOTAL_LINES, MAX_LINES_PER_FILE.
# При необходимости задайте LC_ALL с UTF-8 в окружении.

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LIST="${LIST:-${ROOT}/scripts/diploma-code-files.txt}"
# Ориентир ~22–28 стр. А4 при ~50–55 строках на страницу (при необходимости уменьшите до ~1200).
MAX_TOTAL_LINES="${MAX_TOTAL_LINES:-1520}"
MAX_LINES_PER_FILE="${MAX_LINES_PER_FILE:-200}"

if [[ ! -f "$LIST" ]]; then
  echo "Список файлов не найден: $LIST" >&2
  exit 1
fi

mkdir -p "${ROOT}/build"
OUT="${ROOT}/build/diploma-code-export-$(date +%Y%m%d-%H%M%S).txt"

{
  echo "Выгрузка исходного кода (сокращённый набор для ВКР)"
  echo "ИС подбора и учёта городского пассажирского транспорта"
  echo "Сгенерировано: $(date '+%Y-%m-%d %H:%M:%S %z')"
  echo "Список путей: ${LIST}"
  echo "Лимиты: MAX_TOTAL_LINES=${MAX_TOTAL_LINES}, MAX_LINES_PER_FILE=${MAX_LINES_PER_FILE}"
  echo ""
} >"$OUT"

total="$(wc -l <"$OUT" | tr -d '[:space:]')"
truncated_list=0

while IFS= read -r relpath <&4 || [[ -n "$relpath" ]]; do
  [[ "$relpath" =~ ^[[:space:]]*# ]] && continue
  relpath="${relpath#"${relpath%%[![:space:]]*}"}"
  relpath="${relpath%"${relpath##*[![:space:]]}"}"
  [[ -z "$relpath" ]] && continue

  if ((total >= MAX_TOTAL_LINES)); then
    truncated_list=1
    break
  fi
  # Разделитель (2 строки) и минимум одна строка содержимого или MISSING
  if ((MAX_TOTAL_LINES - total < 3)); then
    truncated_list=1
    break
  fi

  {
    echo ""
    echo "========== ${relpath} =========="
  } >>"$OUT"
  total=$((total + 2))

  abs="${ROOT}/${relpath}"
  if [[ ! -f "$abs" ]]; then
    echo "MISSING: ${relpath}" >>"$OUT"
    total=$((total + 1))
    continue
  fi

  remaining=$((MAX_TOTAL_LINES - total))
  if ((remaining <= 0)); then
    truncated_list=1
    break
  fi

  file_total="$(wc -l <"$abs" | tr -d '[:space:]')"
  limit="$MAX_LINES_PER_FILE"
  if ((file_total < limit)); then
    limit="$file_total"
  fi
  body_cap="$limit"
  if ((body_cap > remaining)); then
    body_cap="$remaining"
  fi
  if ((body_cap < 0)); then
    body_cap=0
  fi

  head -n "$body_cap" "$abs" >>"$OUT"
  total=$((total + body_cap))

  if ((file_total > body_cap)); then
    echo "... [обрезано: выведено ${body_cap} из ${file_total} строк; лимиты MAX_LINES_PER_FILE=${MAX_LINES_PER_FILE}, MAX_TOTAL_LINES=${MAX_TOTAL_LINES}]" >>"$OUT"
    total=$((total + 1))
  fi
done 4<"$LIST"

if [[ "$truncated_list" -eq 1 ]]; then
  {
    echo ""
    echo "[Дальнейшие файлы из списка не включены: достигнут лимит MAX_TOTAL_LINES=${MAX_TOTAL_LINES}]"
  } >>"$OUT"
fi

abs_out="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
printf '%s\n' "$abs_out"
