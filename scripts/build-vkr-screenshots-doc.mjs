#!/usr/bin/env node
/**
 * Собирает PNG из target/cypress/screenshots/.../vkr/ в VKR-screenshots.docx в корне проекта.
 * Запуск: npm install && npm run vkr:docx
 */
import HTMLtoDOCX from 'html-to-docx';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.join(__dirname, '..');

/** Текст из презентации ВКР (Приложение4.md): титул, контекст и блок про интерфейс. */
const INTRO_HTML = `
<p style="text-align:center"><strong>Проектирование и разработка информационной системы по подбору и учёту работы городского пассажирского транспорта</strong></p>
<p style="text-align:center"><strong>Выпускная квалификационная работа</strong></p>
<p style="text-align:center">Студент: Халитов А. М., группа ДЗПИж-205<br/>
Руководитель: Федоров И. А.<br/>
Университет управления «ТИСБИ», 2026</p>
<p><strong>Пользовательский интерфейс</strong></p>
<ul>
<li><strong>Панель (Dashboard):</strong> сводка по парку, отчёт за месяц, показатели по простоям</li>
<li><strong>Подбор ТС (Trip Suggestion):</strong> подбор транспортного средства под маршрут и время</li>
<li><strong>Рейс и путевой лист (Trip / Waybill):</strong> планирование рейсов и учёт выхода на линию</li>
</ul>
<p><em>Ниже приведены скриншоты экранов системы, сформированные автоматически при прогоне E2E-тестов (Cypress) по сценариям пользовательского интерфейса и экранам справочников сущностей.</em></p>
`;

/** Подписи к файлам сущностей — на русском. */
const ENTITY_TITLE = {
  'Entity-Driver': 'Сущность «Водитель» — список записей',
  'Entity-Event': 'Сущность «Событие» — список записей',
  'Entity-RouteStop': 'Сущность «Остановка на маршруте» — список записей',
  'Entity-Route': 'Сущность «Маршрут» — список записей',
  'Entity-Stop': 'Сущность «Остановка» — список записей',
  'Entity-Trip': 'Сущность «Рейс» — список записей',
  'Entity-Vehicle': 'Сущность «Транспортное средство» — список записей',
  'Entity-Waybill': 'Сущность «Путевой лист» — список записей',
};

/** Подписи к кейсам UI (соответствуют UI_TEST_CASES / ui-test-cases.cy.ts). */
const CASE_TITLE = {
  'Case-01-dashboard-fleet': 'Кейс 1. Панель управления: сводка по парку ТС (всего, в работе, в ремонте)',
  'Case-02-suggest-vehicle-success': 'Кейс 2. Подбор ТС: успешный ответ API и отображение предложенного транспорта',
  'Case-03-suggest-vehicle-excludes-busy': 'Кейс 3. Подбор ТС: занятое на пересекающемся рейсе транспортное средство не предлагается',
  'Case-04-trip-driver-overlap-400': 'Кейс 4. Создание рейса: пересечение интервалов у того же водителя — отказ (400)',
  'Case-05-trip-arrival-not-after-departure-400':
    'Кейс 5. Создание рейса: время прибытия не позже времени отправления — отказ (400)',
  'Case-06-vehicle-repair-trip-400': 'Кейс 6. Создание рейса: транспортное средство в ремонте — отказ (400)',
  'Case-07-driver-experience-suburb-400':
    'Кейс 7. Создание рейса: пригородный маршрут и недостаточный стаж водителя — отказ (400)',
  'Case-08-trip-vehicle-overlap-400': 'Кейс 8. Создание рейса: пересечение интервалов у того же ТС — отказ (400)',
  'Case-09-waybill-departure-trip-ongoing':
    'Кейс 9. Путевой лист: регистрация выезда — статус рейса «На линии» (ONGOING)',
  'Case-10-waybill-return-trip-completed':
    'Кейс 10. Путевой лист: регистрация возврата — статус рейса «Завершён» (COMPLETED)',
  'Case-11-waybill-mileage-validation-400': 'Кейс 11. Путевой лист: пробег на возврате меньше пробега на выезде — отказ (400)',
  'Case-12-waybill-return-time-validation-400':
    'Кейс 12. Путевой лист: время возврата раньше времени выезда — отказ (400)',
  'Case-13-fleet-status': 'Кейс 13. Страница «Статус парка»: согласованность с отчётом и списком ТС',
  'Case-14-trips-by-date': 'Кейс 14. Экран «Рейсы по дате»: отображение рейса и статуса за выбранную дату',
  'Case-15-vehicle-schedule': 'Кейс 15а. Расписание транспортного средства за выбранную дату',
  'Case-15-driver-schedule': 'Кейс 15б. Расписание водителя за выбранную дату',
  'Case-16-vehicle-year-detail': 'Кейс 16. Карточка ТС: отображение года выпуска после создания записи',
};

function escapeHtml(s) {
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function pngDimensions(buf) {
  if (buf.length < 24 || buf[0] !== 0x89 || buf.toString('ascii', 1, 4) !== 'PNG') {
    return { width: 1200, height: 800 };
  }
  return { width: buf.readUInt32BE(16), height: buf.readUInt32BE(20) };
}

function listPngs(dir) {
  if (!fs.existsSync(dir)) return [];
  return fs
    .readdirSync(dir)
    .filter(f => f.toLowerCase().endsWith('.png'))
    .sort((a, b) => a.localeCompare(b, undefined, { numeric: true, sensitivity: 'base' }));
}

const SECTIONS = [
  ['Кейсы 1–16 (пользовательский интерфейс, ui-test-cases)', path.join(root, 'target/cypress/screenshots/ui-test-cases.cy.ts', 'vkr')],
  ['Справочники сущностей (vkr-entity-screenshots)', path.join(root, 'target/cypress/screenshots/vkr-entity-screenshots.cy.ts', 'vkr')],
];

const MAX_IMG_WIDTH_PX = 580;

function displayWidthPx(buf) {
  const { width: w0 } = pngDimensions(buf);
  if (w0 <= 0) return MAX_IMG_WIDTH_PX;
  return Math.min(w0, MAX_IMG_WIDTH_PX);
}

function slugFromPngName(file) {
  return file.replace(/\.png$/i, '');
}

function captionForPng(file) {
  const slug = slugFromPngName(file);
  if (ENTITY_TITLE[slug]) return ENTITY_TITLE[slug];
  if (CASE_TITLE[slug]) return CASE_TITLE[slug];
  return slug.replace(/-/g, ' — ');
}

const htmlParts = [
  '<!DOCTYPE html><html><head><meta charset="utf-8"></head>',
  `<body style="font-family:'Times New Roman',Times,serif;font-size:14pt">`,
  '<h1 style="text-align:center;font-family:\'Times New Roman\',Times,serif">Скриншоты интерфейса информационной системы</h1>',
  INTRO_HTML.trim(),
];

let totalImages = 0;

for (const [sectionTitle, dir] of SECTIONS) {
  htmlParts.push(`<h2 style="font-family:'Times New Roman',Times,serif">${escapeHtml(sectionTitle)}</h2>`);

  const files = listPngs(dir);
  if (files.length === 0) {
    htmlParts.push(`<p><em>Нет PNG в: ${escapeHtml(path.relative(root, dir))}</em></p>`);
    continue;
  }

  for (const file of files) {
    const fp = path.join(dir, file);
    const buf = fs.readFileSync(fp);
    const w = displayWidthPx(buf);
    const caption = captionForPng(file);
    const b64 = buf.toString('base64');
    const dataUri = `data:image/png;base64,${b64}`;

    htmlParts.push(`<h3 style="font-family:'Times New Roman',Times,serif">${escapeHtml(caption)}</h3>`);
    htmlParts.push(`<p><img src="${dataUri}" width="${w}" alt="${escapeHtml(caption)}" /></p>`);
    totalImages += 1;
  }
}

htmlParts.push('</body></html>');

if (totalImages === 0) {
  console.error('Не найдено ни одного PNG. Сначала прогоните Cypress и проверьте пути:');
  for (const [, d] of SECTIONS) console.error(' ', d);
  process.exit(1);
}

const html = htmlParts.join('\n');

/** В html-to-docx размер шрифта задаётся в полупунктах (half-points): 14 pt → 28. */
const buffer = await HTMLtoDOCX(html, null, {
  title: 'Скриншоты интерфейса ИС (ВКР)',
  creator: 'npm run vkr:docx',
  description: 'ИС подбора и учёта городского пассажирского транспорта — скриншоты Cypress',
  font: 'Times New Roman',
  fontSize: 28,
});

const outPath = path.join(root, 'VKR-screenshots.docx');
fs.writeFileSync(outPath, buffer);
console.log(`Готово: ${outPath} (${totalImages} изображений)`);
