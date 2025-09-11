-- SourceDB PostgreSQL Database Conversion
-- Converted from YouLai_Boot MySQL Database
-- Copyright (c) 2021-present, youlai.tech
-- Database: sourcedb (PostgreSQL 12+)

-- ----------------------------
-- 1. Create database
-- ----------------------------
-- Note: Run this separately as a superuser or database owner
-- CREATE DATABASE sourcedb WITH ENCODING 'UTF8' LC_COLLATE 'en_US.UTF-8' LC_CTYPE 'en_US.UTF-8';

-- Connect to sourcedb database before running the rest of the script
-- \c sourcedb;

-- ----------------------------
-- 2. Create tables && Initialize data
-- ----------------------------

-- Set timezone
SET timezone = 'UTC';

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS sys_dept CASCADE;
CREATE TABLE sys_dept (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    tree_path VARCHAR(255) NOT NULL,
    sort SMALLINT DEFAULT 0,
    status SMALLINT DEFAULT 1,
    create_by BIGINT,
    create_time TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

-- Add unique constraint
CREATE UNIQUE INDEX uk_code ON sys_dept(code);

-- Add comment
COMMENT ON TABLE sys_dept IS '部門表 (Department Table)';
COMMENT ON COLUMN sys_dept.id IS '主鍵 (Primary Key)';
COMMENT ON COLUMN sys_dept.name IS '部門名稱 (Department Name)';
COMMENT ON COLUMN sys_dept.code IS '部門編號 (Department Code)';
COMMENT ON COLUMN sys_dept.parent_id IS '父節點id (Parent Node ID)';
COMMENT ON COLUMN sys_dept.tree_path IS '父節點id路徑 (Parent Node Path)';
COMMENT ON COLUMN sys_dept.sort IS '顯示順序 (Display Order)';
COMMENT ON COLUMN sys_dept.status IS '狀態(1-正常 0-禁用) (Status: 1-Normal, 0-Disabled)';
COMMENT ON COLUMN sys_dept.create_by IS '建立人ID (Created By)';
COMMENT ON COLUMN sys_dept.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_dept.update_by IS '修改人ID (Updated By)';
COMMENT ON COLUMN sys_dept.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN sys_dept.is_deleted IS '邏輯刪除標識(1-已刪除 0-未刪除) (Logical Delete Flag: 1-Deleted, 0-Not Deleted)';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO sys_dept (id, name, code, parent_id, tree_path, sort, status, create_by, create_time, update_by, update_time, is_deleted) VALUES 
(1, '有來技術', 'YOULAI', 0, '0', 1, 1, 1, NOW(), 1, NOW(), 0),
(2, '研發部門', 'RD001', 1, '0,1', 1, 1, 2, NOW(), 2, NOW(), 0),
(3, '測試部門', 'QA001', 1, '0,1', 1, 1, 2, NOW(), 2, NOW(), 0);

-- Reset sequence
SELECT setval('sys_dept_id_seq', (SELECT MAX(id) FROM sys_dept));

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS sys_dict CASCADE;
CREATE TABLE sys_dict (
    id BIGSERIAL PRIMARY KEY,
    dict_code VARCHAR(50),
    name VARCHAR(50),
    status SMALLINT DEFAULT 0,
    remark VARCHAR(255),
    create_time TIMESTAMP,
    create_by BIGINT,
    update_time TIMESTAMP,
    update_by BIGINT,
    is_deleted SMALLINT DEFAULT 0
);

-- Add index
CREATE INDEX idx_dict_code ON sys_dict(dict_code);

-- Add comments
COMMENT ON TABLE sys_dict IS '字典表 (Dictionary Table)';
COMMENT ON COLUMN sys_dict.id IS '主鍵 (Primary Key)';
COMMENT ON COLUMN sys_dict.dict_code IS '型別編碼 (Type Code)';
COMMENT ON COLUMN sys_dict.name IS '型別名稱 (Type Name)';
COMMENT ON COLUMN sys_dict.status IS '狀態(0:正常;1:禁用) (Status: 0-Normal; 1-Disabled)';
COMMENT ON COLUMN sys_dict.remark IS '備註 (Remark)';
COMMENT ON COLUMN sys_dict.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_dict.create_by IS '建立人ID (Created By)';
COMMENT ON COLUMN sys_dict.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN sys_dict.update_by IS '修改人ID (Updated By)';
COMMENT ON COLUMN sys_dict.is_deleted IS '是否刪除(1-刪除，0-未刪除) (Is Deleted: 1-Deleted, 0-Not Deleted)';

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO sys_dict (id, dict_code, name, status, remark, create_time, create_by, update_time, update_by, is_deleted) VALUES 
(1, 'gender', '性別', 1, NULL, NOW(), 1, NOW(), 1, 0),
(2, 'notice_type', '通知型別', 1, NULL, NOW(), 1, NOW(), 1, 0),
(3, 'notice_level', '通知級別', 1, NULL, NOW(), 1, NOW(), 1, 0);

-- Reset sequence
SELECT setval('sys_dict_id_seq', (SELECT MAX(id) FROM sys_dict));

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_item CASCADE;
CREATE TABLE sys_dict_item (
    id BIGSERIAL PRIMARY KEY,
    dict_code VARCHAR(50),
    value VARCHAR(50),
    label VARCHAR(100),
    tag_type VARCHAR(50),
    status SMALLINT DEFAULT 0,
    sort INTEGER DEFAULT 0,
    remark VARCHAR(255),
    create_time TIMESTAMP,
    create_by BIGINT,
    update_time TIMESTAMP,
    update_by BIGINT
);

-- Add comments
COMMENT ON TABLE sys_dict_item IS '字典項表 (Dictionary Item Table)';
COMMENT ON COLUMN sys_dict_item.id IS '主鍵 (Primary Key)';
COMMENT ON COLUMN sys_dict_item.dict_code IS '關聯字典編碼，與sys_dict表中的dict_code對應 (Related Dictionary Code, corresponds to dict_code in sys_dict)';
COMMENT ON COLUMN sys_dict_item.value IS '字典項值 (Item Value)';
COMMENT ON COLUMN sys_dict_item.label IS '字典項標籤 (Item Label)';
COMMENT ON COLUMN sys_dict_item.tag_type IS '標籤型別，用於前端樣式展示（如success、warning等） (Tag Type, for frontend style e.g. success, warning)';
COMMENT ON COLUMN sys_dict_item.status IS '狀態（1-正常，0-禁用） (Status: 1-Normal, 0-Disabled)';
COMMENT ON COLUMN sys_dict_item.sort IS '排序 (Sort Order)';
COMMENT ON COLUMN sys_dict_item.remark IS '備註 (Remark)';
COMMENT ON COLUMN sys_dict_item.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_dict_item.create_by IS '建立人ID (Created By)';
COMMENT ON COLUMN sys_dict_item.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN sys_dict_item.update_by IS '修改人ID (Updated By)';

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO sys_dict_item (id, dict_code, value, label, tag_type, status, sort, remark, create_time, create_by, update_time, update_by) VALUES 
(1, 'gender', '1', '男', 'primary', 1, 1, NULL, NOW(), 1, NOW(), 1),
(2, 'gender', '2', '女', 'danger', 1, 2, NULL, NOW(), 1, NOW(), 1),
(3, 'gender', '0', '保密', 'info', 1, 3, NULL, NOW(), 1, NOW(), 1),
(4, 'notice_type', '1', '系統升級', 'success', 1, 1, '', NOW(), 1, NOW(), 1),
(5, 'notice_type', '2', '系統維護', 'primary', 1, 2, '', NOW(), 1, NOW(), 1),
(6, 'notice_type', '3', '安全警告', 'danger', 1, 3, '', NOW(), 1, NOW(), 1),
(7, 'notice_type', '4', '假期通知', 'success', 1, 4, '', NOW(), 1, NOW(), 1),
(8, 'notice_type', '5', '公司新聞', 'primary', 1, 5, '', NOW(), 1, NOW(), 1),
(9, 'notice_type', '99', '其他', 'info', 1, 99, '', NOW(), 1, NOW(), 1),
(10, 'notice_level', 'L', '低', 'info', 1, 1, '', NOW(), 1, NOW(), 1),
(11, 'notice_level', 'M', '中', 'warning', 1, 2, '', NOW(), 1, NOW(), 1),
(12, 'notice_level', 'H', '高', 'danger', 1, 3, '', NOW(), 1, NOW(), 1);

-- Reset sequence
SELECT setval('sys_dict_item_id_seq', (SELECT MAX(id) FROM sys_dict_item));

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_menu CASCADE;
CREATE TABLE sys_menu (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT NOT NULL,
    tree_path VARCHAR(255),
    name VARCHAR(64) NOT NULL,
    type SMALLINT NOT NULL,
    route_name VARCHAR(255),
    route_path VARCHAR(128),
    component VARCHAR(128),
    perm VARCHAR(128),
    always_show SMALLINT DEFAULT 0,
    keep_alive SMALLINT DEFAULT 0,
    visible SMALLINT DEFAULT 1,
    sort INTEGER DEFAULT 0,
    icon VARCHAR(64),
    redirect VARCHAR(128),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    params VARCHAR(255)
);

-- Add comments
COMMENT ON TABLE sys_menu IS '選單管理 (Menu Management)';
COMMENT ON COLUMN sys_menu.id IS 'ID';
COMMENT ON COLUMN sys_menu.parent_id IS '父選單ID (Parent Menu ID)';
COMMENT ON COLUMN sys_menu.tree_path IS '父節點ID路徑 (Parent Node Path)';
COMMENT ON COLUMN sys_menu.name IS '選單名稱 (Menu Name)';
COMMENT ON COLUMN sys_menu.type IS '選單型別（1-選單 2-目錄 3-外鏈 4-按鈕） (Menu Type: 1-Menu 2-Directory 3-External Link 4-Button)';
COMMENT ON COLUMN sys_menu.route_name IS '路由名稱（Vue Router 中用於命名路由） (Route Name, for Vue Router)';
COMMENT ON COLUMN sys_menu.route_path IS '路由路徑（Vue Router 中定義的 URL 路徑） (Route Path, as defined in Vue Router)';
COMMENT ON COLUMN sys_menu.component IS '元件路徑（元件頁面完整路徑，相對於 src/views/，預設字尾 .vue） (Component Path, relative to src/views/, default suffix .vue)';
COMMENT ON COLUMN sys_menu.perm IS '【按鈕】許可權標識 (Button Permission Identifier)';
COMMENT ON COLUMN sys_menu.always_show IS '【目錄】只有一個子路由是否始終顯示（1-是 0-否） (Directory: Always Show if Only One Child Route: 1-Yes 0-No)';
COMMENT ON COLUMN sys_menu.keep_alive IS '【選單】是否開啟頁面快取（1-是 0-否） (Menu: Enable Page Cache: 1-Yes 0-No)';
COMMENT ON COLUMN sys_menu.visible IS '顯示狀態（1-顯示 0-隱藏） (Visible Status: 1-Show 0-Hide)';
COMMENT ON COLUMN sys_menu.sort IS '排序 (Sort Order)';
COMMENT ON COLUMN sys_menu.icon IS '選單圖示 (Menu Icon)';
COMMENT ON COLUMN sys_menu.redirect IS '跳轉路徑 (Redirect Path)';
COMMENT ON COLUMN sys_menu.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_menu.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN sys_menu.params IS '路由引數 (Route Parameters)';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO sys_menu (id, parent_id, tree_path, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time, update_time, params) VALUES 
(1, 0, '0', '系統管理', 2, '', '/system', 'Layout', NULL, NULL, NULL, 1, 1, 'system', '/system/user', NOW(), NOW(), NULL),
(2, 1, '0,1', '使用者管理', 1, 'User', 'user', 'system/user/index', NULL, NULL, 1, 1, 1, 'el-icon-User', NULL, NOW(), NOW(), NULL),
(3, 1, '0,1', '角色管理', 1, 'Role', 'role', 'system/role/index', NULL, NULL, 1, 1, 2, 'role', NULL, NOW(), NOW(), NULL),
(4, 1, '0,1', '選單管理', 1, 'SysMenu', 'menu', 'system/menu/index', NULL, NULL, 1, 1, 3, 'menu', NULL, NOW(), NOW(), NULL),
(5, 1, '0,1', '部門管理', 1, 'Dept', 'dept', 'system/dept/index', NULL, NULL, 1, 1, 4, 'tree', NULL, NOW(), NOW(), NULL),
(6, 1, '0,1', '字典管理', 1, 'Dict', 'dict', 'system/dict/index', NULL, NULL, 1, 1, 5, 'dict', NULL, NOW(), NOW(), NULL),
(20, 0, '0', '多級選單', 2, NULL, '/multi-level', 'Layout', NULL, 1, NULL, 1, 9, 'cascader', '', NOW(), NOW(), NULL),
(21, 20, '0,20', '選單一級', 1, NULL, 'multi-level1', 'demo/multi-level/level1', NULL, 1, NULL, 1, 1, '', '', NOW(), NOW(), NULL),
(22, 21, '0,20,21', '選單二級', 1, NULL, 'multi-level2', 'demo/multi-level/children/level2', NULL, 0, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(23, 22, '0,20,21,22', '選單三級-1', 1, NULL, 'multi-level3-1', 'demo/multi-level/children/children/level3-1', NULL, 0, 1, 1, 1, '', '', NOW(), NOW(), NULL),
(24, 22, '0,20,21,22', '選單三級-2', 1, NULL, 'multi-level3-2', 'demo/multi-level/children/children/level3-2', NULL, 0, 1, 1, 2, '', '', NOW(), NOW(), NULL),
(26, 0, '0', '平臺文件', 2, '', '/doc', 'Layout', NULL, NULL, NULL, 1, 8, 'document', 'https://juejin.cn/post/7228990409909108793', NOW(), NOW(), NULL),
(30, 26, '0,26', '平臺文件(外鏈)', 3, NULL, 'https://juejin.cn/post/7228990409909108793', '', NULL, NULL, NULL, 1, 2, 'document', '', NOW(), NOW(), NULL),
(31, 2, '0,1,2', '使用者新增', 4, NULL, '', NULL, 'sys:user:add', NULL, NULL, 1, 1, '', '', NOW(), NOW(), NULL),
(32, 2, '0,1,2', '使用者編輯', 4, NULL, '', NULL, 'sys:user:edit', NULL, NULL, 1, 2, '', '', NOW(), NOW(), NULL),
(33, 2, '0,1,2', '使用者刪除', 4, NULL, '', NULL, 'sys:user:delete', NULL, NULL, 1, 3, '', '', NOW(), NOW(), NULL),
(36, 0, '0', '元件封裝', 2, NULL, '/component', 'Layout', NULL, NULL, NULL, 1, 10, 'menu', '', NOW(), NOW(), NULL),
(37, 36, '0,36', '富文字編輯器', 1, NULL, 'wang-editor', 'demo/wang-editor', NULL, NULL, 1, 1, 2, '', '', NULL, NULL, NULL),
(38, 36, '0,36', '圖片上傳', 1, NULL, 'upload', 'demo/upload', NULL, NULL, 1, 1, 3, '', '', NOW(), NOW(), NULL),
(39, 36, '0,36', '圖示選擇器', 1, NULL, 'icon-selector', 'demo/icon-selector', NULL, NULL, 1, 1, 4, '', '', NOW(), NOW(), NULL),
(40, 0, '0', '介面文件', 2, NULL, '/api', 'Layout', NULL, 1, NULL, 1, 7, 'api', '', NOW(), NOW(), NULL),
(41, 40, '0,40', 'Apifox', 1, NULL, 'apifox', 'demo/api/apifox', NULL, NULL, 1, 1, 1, 'api', '', NOW(), NOW(), NULL),
(70, 3, '0,1,3', '角色新增', 4, NULL, '', NULL, 'sys:role:add', NULL, NULL, 1, 2, '', NULL, NOW(), NOW(), NULL),
(71, 3, '0,1,3', '角色編輯', 4, NULL, '', NULL, 'sys:role:edit', NULL, NULL, 1, 3, '', NULL, NOW(), NOW(), NULL),
(72, 3, '0,1,3', '角色刪除', 4, NULL, '', NULL, 'sys:role:delete', NULL, NULL, 1, 4, '', NULL, NOW(), NOW(), NULL),
(73, 4, '0,1,4', '選單新增', 4, NULL, '', NULL, 'sys:menu:add', NULL, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(74, 4, '0,1,4', '選單編輯', 4, NULL, '', NULL, 'sys:menu:edit', NULL, NULL, 1, 3, '', NULL, NOW(), NOW(), NULL),
(75, 4, '0,1,4', '選單刪除', 4, NULL, '', NULL, 'sys:menu:delete', NULL, NULL, 1, 3, '', NULL, NOW(), NOW(), NULL),
(76, 5, '0,1,5', '部門新增', 4, NULL, '', NULL, 'sys:dept:add', NULL, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(77, 5, '0,1,5', '部門編輯', 4, NULL, '', NULL, 'sys:dept:edit', NULL, NULL, 1, 2, '', NULL, NOW(), NOW(), NULL),
(78, 5, '0,1,5', '部門刪除', 4, NULL, '', NULL, 'sys:dept:delete', NULL, NULL, 1, 3, '', NULL, NOW(), NOW(), NULL),
(79, 6, '0,1,6', '字典新增', 4, NULL, '', NULL, 'sys:dict:add', NULL, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(81, 6, '0,1,6', '字典編輯', 4, NULL, '', NULL, 'sys:dict:edit', NULL, NULL, 1, 2, '', NULL, NOW(), NOW(), NULL),
(84, 6, '0,1,6', '字典刪除', 4, NULL, '', NULL, 'sys:dict:delete', NULL, NULL, 1, 3, '', NULL, NOW(), NOW(), NULL),
(88, 2, '0,1,2', '重置密碼', 4, NULL, '', NULL, 'sys:user:reset-password', NULL, NULL, 1, 4, '', NULL, NOW(), NOW(), NULL),
(89, 0, '0', '功能演示', 2, NULL, '/function', 'Layout', NULL, NULL, NULL, 1, 12, 'menu', '', NOW(), NOW(), NULL),
(90, 89, '0,89', 'Websocket', 1, NULL, '/function/websocket', 'demo/websocket', NULL, NULL, 1, 1, 3, '', '', NOW(), NOW(), NULL),
(95, 36, '0,36', '字典元件', 1, NULL, 'dict-demo', 'demo/dictionary', NULL, NULL, 1, 1, 4, '', '', NOW(), NOW(), NULL),
(97, 89, '0,89', 'Icons', 1, NULL, 'icon-demo', 'demo/icons', NULL, NULL, 1, 1, 2, 'el-icon-Notification', '', NOW(), NOW(), NULL),
(102, 26, '0,26', 'document', 3, '', 'internal-doc', 'demo/internal-doc', NULL, NULL, NULL, 1, 1, 'document', '', NOW(), NOW(), NULL),
(105, 2, '0,1,2', '使用者查詢', 4, NULL, '', NULL, 'sys:user:query', 0, 0, 1, 0, '', NULL, NOW(), NOW(), NULL),
(106, 2, '0,1,2', '使用者匯入', 4, NULL, '', NULL, 'sys:user:import', NULL, NULL, 1, 5, '', NULL, NOW(), NOW(), NULL),
(107, 2, '0,1,2', '使用者匯出', 4, NULL, '', NULL, 'sys:user:export', NULL, NULL, 1, 6, '', NULL, NOW(), NOW(), NULL),
(108, 36, '0,36', '增刪改查', 1, NULL, 'curd', 'demo/curd/index', NULL, NULL, 1, 1, 0, '', '', NULL, NULL, NULL),
(109, 36, '0,36', '列表選擇器', 1, NULL, 'table-select', 'demo/table-select/index', NULL, NULL, 1, 1, 1, '', '', NULL, NULL, NULL),
(110, 0, '0', '路由引數', 2, NULL, '/route-param', 'Layout', NULL, 1, 1, 1, 11, 'el-icon-ElementPlus', NULL, NOW(), NOW(), NULL),
(111, 110, '0,110', '引數(type=1)', 1, NULL, 'route-param-type1', 'demo/route-param', NULL, 0, 1, 1, 1, 'el-icon-Star', NULL, NOW(), NOW(), '{"type": "1"}'),
(112, 110, '0,110', '引數(type=2)', 1, NULL, 'route-param-type2', 'demo/route-param', NULL, 0, 1, 1, 2, 'el-icon-StarFilled', NULL, NOW(), NOW(), '{"type": "2"}'),
(117, 1, '0,1', '系統日誌', 1, 'Log', 'log', 'system/log/index', NULL, 0, 1, 1, 6, 'document', NULL, NOW(), NOW(), NULL),
(118, 0, '0', '系統工具', 2, NULL, '/codegen', 'Layout', NULL, 0, 1, 1, 2, 'menu', NULL, NOW(), NOW(), NULL),
(119, 118, '0,118', '程式碼生成', 1, 'Codegen', 'codegen', 'codegen/index', NULL, 0, 1, 1, 1, 'code', NULL, NOW(), NOW(), NULL),
(120, 1, '0,1', '系統配置', 1, 'Config', 'config', 'system/config/index', NULL, 0, 1, 1, 7, 'setting', NULL, NOW(), NOW(), NULL),
(121, 120, '0,1,120', '系統配置查詢', 4, NULL, '', NULL, 'sys:config:query', 0, 1, 1, 1, '', NULL, NOW(), NOW(), NULL),
(122, 120, '0,1,120', '系統配置新增', 4, NULL, '', NULL, 'sys:config:add', 0, 1, 1, 2, '', NULL, NOW(), NOW(), NULL),
(123, 120, '0,1,120', '系統配置修改', 4, NULL, '', NULL, 'sys:config:update', 0, 1, 1, 3, '', NULL, NOW(), NOW(), NULL),
(124, 120, '0,1,120', '系統配置刪除', 4, NULL, '', NULL, 'sys:config:delete', 0, 1, 1, 4, '', NULL, NOW(), NOW(), NULL),
(125, 120, '0,1,120', '系統配置重新整理', 4, NULL, '', NULL, 'sys:config:refresh', 0, 1, 1, 5, '', NULL, NOW(), NOW(), NULL),
(126, 1, '0,1', '通知公告', 1, 'Notice', 'notice', 'system/notice/index', NULL, NULL, NULL, 1, 9, '', NULL, NOW(), NOW(), NULL),
(127, 126, '0,1,126', '通知查詢', 4, NULL, '', NULL, 'sys:notice:query', NULL, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(128, 126, '0,1,126', '通知新增', 4, NULL, '', NULL, 'sys:notice:add', NULL, NULL, 1, 2, '', NULL, NOW(), NOW(), NULL),
(129, 126, '0,1,126', '通知編輯', 4, NULL, '', NULL, 'sys:notice:edit', NULL, NULL, 1, 3, '', NULL, NOW(), NOW(), NULL),
(130, 126, '0,1,126', '通知刪除', 4, NULL, '', NULL, 'sys:notice:delete', NULL, NULL, 1, 4, '', NULL, NOW(), NOW(), NULL),
(133, 126, '0,1,126', '通知釋出', 4, NULL, '', NULL, 'sys:notice:publish', 0, 1, 1, 5, '', NULL, NOW(), NOW(), NULL),
(134, 126, '0,1,126', '通知撤回', 4, NULL, '', NULL, 'sys:notice:revoke', 0, 1, 1, 6, '', NULL, NOW(), NOW(), NULL),
(135, 1, '0,1', '字典項', 1, 'DictItem', 'dict-item', 'system/dict/dict-item', NULL, 0, 1, 0, 6, '', NULL, NOW(), NOW(), NULL),
(136, 135, '0,1,135', '字典項新增', 4, NULL, '', NULL, 'sys:dict-item:add', NULL, NULL, 1, 2, '', NULL, NOW(), NOW(), NULL),
(137, 135, '0,1,135', '字典項編輯', 4, NULL, '', NULL, 'sys:dict-item:edit', NULL, NULL, 1, 3, '', NULL, NOW(), NOW(), NULL),
(138, 135, '0,1,135', '字典項刪除', 4, NULL, '', NULL, 'sys:dict-item:delete', NULL, NULL, 1, 4, '', NULL, NOW(), NOW(), NULL),
(139, 3, '0,1,3', '角色查詢', 4, NULL, '', NULL, 'sys:role:query', NULL, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(140, 4, '0,1,4', '選單查詢', 4, NULL, '', NULL, 'sys:menu:query', NULL, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(141, 5, '0,1,5', '部門查詢', 4, NULL, '', NULL, 'sys:dept:query', NULL, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(142, 6, '0,1,6', '字典查詢', 4, NULL, '', NULL, 'sys:dict:query', NULL, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(143, 135, '0,1,135', '字典項查詢', 4, NULL, '', NULL, 'sys:dict-item:query', NULL, NULL, 1, 1, '', NULL, NOW(), NOW(), NULL),
(144, 26, '0,26', '後端文件', 3, NULL, 'https://youlai.blog.csdn.net/article/details/145178880', '', NULL, NULL, NULL, 1, 3, 'document', '', NOW(), NOW(), NULL),
(145, 26, '0,26', '移動端文件', 3, NULL, 'https://youlai.blog.csdn.net/article/details/143222890', '', NULL, NULL, NULL, 1, 4, 'document', '', NOW(), NOW(), NULL),
(146, 36, '0,36', '拖拽元件', 1, NULL, 'drag', 'demo/drag', NULL, NULL, NULL, 1, 5, '', '', NOW(), NOW(), NULL),
(147, 36, '0,36', '滾動文字', 1, NULL, 'text-scroll', 'demo/text-scroll', NULL, NULL, NULL, 1, 6, '', '', NOW(), NOW(), NULL),
(148, 89, '0,89', '字典實時同步', 1, NULL, 'dict-sync', 'demo/dict-sync', NULL, NULL, NULL, 1, 3, '', '', NOW(), NOW(), NULL);

-- Reset sequence
SELECT setval('sys_menu_id_seq', (SELECT MAX(id) FROM sys_menu));

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS sys_role CASCADE;
CREATE TABLE sys_role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    code VARCHAR(32) NOT NULL,
    sort INTEGER,
    status SMALLINT DEFAULT 1,
    data_scope SMALLINT,
    create_by BIGINT,
    create_time TIMESTAMP,
    update_by BIGINT,
    update_time TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

-- Add unique constraints
CREATE UNIQUE INDEX uk_name ON sys_role(name);
CREATE UNIQUE INDEX uk_sys_role_code ON sys_role(code);

-- Add comments
COMMENT ON TABLE sys_role IS '角色表 (Role Table)';
COMMENT ON COLUMN sys_role.name IS '角色名稱 (Role Name)';
COMMENT ON COLUMN sys_role.code IS '角色編碼 (Role Code)';
COMMENT ON COLUMN sys_role.sort IS '顯示順序 (Display Order)';
COMMENT ON COLUMN sys_role.status IS '角色狀態(1-正常 0-停用) (Role Status: 1-Active 0-Inactive)';
COMMENT ON COLUMN sys_role.data_scope IS '資料許可權(1-所有資料 2-部門及子部門資料 3-本部門資料 4-本人資料) (Data Scope: 1-All 2-Dept & Sub 3-Dept 4-Self)';
COMMENT ON COLUMN sys_role.create_by IS '建立人 ID (Created By)';
COMMENT ON COLUMN sys_role.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_role.update_by IS '更新人ID (Updated By)';
COMMENT ON COLUMN sys_role.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN sys_role.is_deleted IS '邏輯刪除標識(0-未刪除 1-已刪除) (Logical Delete: 0-Not Deleted 1-Deleted)';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO sys_role (id, name, code, sort, status, data_scope, create_by, create_time, update_by, update_time, is_deleted) VALUES 
(1, '超級管理員', 'ROOT', 1, 1, 1, NULL, NOW(), NULL, NOW(), 0),
(2, '系統管理員', 'ADMIN', 2, 1, 1, NULL, NOW(), NULL, NULL, 0),
(3, '訪問遊客', 'GUEST', 3, 1, 3, NULL, NOW(), NULL, NOW(), 0),
(4, '系統管理員1', 'ADMIN1', 4, 1, 1, NULL, NOW(), NULL, NULL, 0),
(5, '系統管理員2', 'ADMIN2', 5, 1, 1, NULL, NOW(), NULL, NULL, 0),
(6, '系統管理員3', 'ADMIN3', 6, 1, 1, NULL, NOW(), NULL, NULL, 0),
(7, '系統管理員4', 'ADMIN4', 7, 1, 1, NULL, NOW(), NULL, NULL, 0),
(8, '系統管理員5', 'ADMIN5', 8, 1, 1, NULL, NOW(), NULL, NULL, 0),
(9, '系統管理員6', 'ADMIN6', 9, 1, 1, NULL, NOW(), NULL, NULL, 0),
(10, '系統管理員7', 'ADMIN7', 10, 1, 1, NULL, NOW(), NULL, NULL, 0),
(11, '系統管理員8', 'ADMIN8', 11, 1, 1, NULL, NOW(), NULL, NULL, 0),
(12, '系統管理員9', 'ADMIN9', 12, 1, 1, NULL, NOW(), NULL, NULL, 0);

-- Reset sequence
SELECT setval('sys_role_id_seq', (SELECT MAX(id) FROM sys_role));

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu CASCADE;
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    CONSTRAINT uk_roleid_menuid UNIQUE (role_id, menu_id)
);

-- Add comments
COMMENT ON TABLE sys_role_menu IS '角色和選單關聯表 (Role-Menu Relation Table)';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID (Role ID)';
COMMENT ON COLUMN sys_role_menu.menu_id IS '選單ID (Menu ID)';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO sys_role_menu (role_id, menu_id) VALUES 
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 20), (2, 21), (2, 22), (2, 23), (2, 24), (2, 26), (2, 30), (2, 31), (2, 32), (2, 33), 
(2, 36), (2, 37), (2, 38), (2, 39), (2, 40), (2, 41), (2, 70), (2, 71), (2, 72), (2, 73), (2, 74), (2, 75), (2, 76), (2, 77), (2, 78), (2, 79), 
(2, 81), (2, 84), (2, 88), (2, 89), (2, 90), (2, 95), (2, 97), (2, 102), (2, 105), (2, 106), (2, 107), (2, 108), (2, 109), (2, 110), (2, 111), 
(2, 112), (2, 117), (2, 118), (2, 119), (2, 120), (2, 121), (2, 122), (2, 123), (2, 124), (2, 125), (2, 126), (2, 127), (2, 128), (2, 129), 
(2, 130), (2, 133), (2, 134), (2, 135), (2, 136), (2, 137), (2, 138), (2, 139), (2, 140), (2, 141), (2, 142), (2, 143), (2, 144), (2, 145), 
(2, 146), (2, 147), (2, 148);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS sys_user CASCADE;
CREATE TABLE sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64),
    nickname VARCHAR(64),
    gender SMALLINT DEFAULT 1,
    password VARCHAR(100),
    dept_id INTEGER,
    avatar VARCHAR(255),
    mobile VARCHAR(20),
    status SMALLINT DEFAULT 1,
    email VARCHAR(128),
    create_time TIMESTAMP,
    create_by BIGINT,
    update_time TIMESTAMP,
    update_by BIGINT,
    is_deleted SMALLINT DEFAULT 0,
    openid CHAR(28)
);

-- Add index
CREATE INDEX login_name ON sys_user(username);

-- Add comments
COMMENT ON TABLE sys_user IS '使用者資訊表 (User Info Table)';
COMMENT ON COLUMN sys_user.username IS '使用者名稱 (Username)';
COMMENT ON COLUMN sys_user.nickname IS '暱稱 (Nickname)';
COMMENT ON COLUMN sys_user.gender IS '性別((1-男 2-女 0-保密) (Gender: 1-Male 2-Female 0-Secret)';
COMMENT ON COLUMN sys_user.password IS '密碼 (Password)';
COMMENT ON COLUMN sys_user.dept_id IS '部門ID (Department ID)';
COMMENT ON COLUMN sys_user.avatar IS '使用者頭像 (Avatar)';
COMMENT ON COLUMN sys_user.mobile IS '聯絡方式 (Mobile)';
COMMENT ON COLUMN sys_user.status IS '狀態(1-正常 0-禁用) (Status: 1-Active 0-Inactive)';
COMMENT ON COLUMN sys_user.email IS '使用者郵箱 (Email)';
COMMENT ON COLUMN sys_user.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_user.create_by IS '建立人ID (Created By)';
COMMENT ON COLUMN sys_user.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN sys_user.update_by IS '修改人ID (Updated By)';
COMMENT ON COLUMN sys_user.is_deleted IS '邏輯刪除標識(0-未刪除 1-已刪除) (Logical Delete: 0-Not Deleted 1-Deleted)';
COMMENT ON COLUMN sys_user.openid IS '微信 openid (WeChat openid)';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO sys_user (id, username, nickname, gender, password, dept_id, avatar, mobile, status, email, create_time, create_by, update_time, update_by, is_deleted, openid) VALUES 
(1, 'root', '有來技術', 0, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', NULL, 'https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif', '18812345677', 1, 'youlaitech@163.com', NOW(), NULL, NOW(), NULL, 0, NULL),
(2, 'admin', '系統管理員', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 1, 'https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif', '18812345678', 1, 'youlaitech@163.com', NOW(), NULL, NOW(), NULL, 0, NULL),
(3, 'test', '測試小使用者', 1, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', 3, 'https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif', '18812345679', 1, 'youlaitech@163.com', NOW(), NULL, NOW(), NULL, 0, NULL);

-- Reset sequence
SELECT setval('sys_user_id_seq', (SELECT MAX(id) FROM sys_user));

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role CASCADE;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- Add comments
COMMENT ON TABLE sys_user_role IS '使用者和角色關聯表 (User-Role Relation Table)';
COMMENT ON COLUMN sys_user_role.user_id IS '使用者ID (User ID)';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID (Role ID)';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO sys_user_role (user_id, role_id) VALUES 
(1, 1),
(2, 2),
(3, 3);

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS sys_log CASCADE;
CREATE TABLE sys_log (
    id BIGSERIAL PRIMARY KEY,
    module VARCHAR(50) NOT NULL,
    request_method VARCHAR(64) NOT NULL,
    request_params TEXT,
    response_content TEXT,
    content VARCHAR(255) NOT NULL,
    request_uri VARCHAR(255),
    method VARCHAR(255),
    ip VARCHAR(45),
    province VARCHAR(100),
    city VARCHAR(100),
    execution_time BIGINT,
    browser VARCHAR(100),
    browser_version VARCHAR(100),
    os VARCHAR(100),
    create_by BIGINT,
    create_time TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

-- Add index
CREATE INDEX idx_create_time ON sys_log(create_time);

-- Add comments
COMMENT ON TABLE sys_log IS '系統日誌表 (System Log Table)';
COMMENT ON COLUMN sys_log.id IS '主鍵 (Primary Key)';
COMMENT ON COLUMN sys_log.module IS '日誌模組 (Log Module)';
COMMENT ON COLUMN sys_log.request_method IS '請求方式 (Request Method)';
COMMENT ON COLUMN sys_log.request_params IS '請求引數(批次請求引數可能會超過text) (Request Params, may exceed text for batch)';
COMMENT ON COLUMN sys_log.response_content IS '返回引數 (Response Content)';
COMMENT ON COLUMN sys_log.content IS '日誌內容 (Log Content)';
COMMENT ON COLUMN sys_log.request_uri IS '請求路徑 (Request URI)';
COMMENT ON COLUMN sys_log.method IS '方法名 (Method Name)';
COMMENT ON COLUMN sys_log.ip IS 'IP地址 (IP Address)';
COMMENT ON COLUMN sys_log.province IS '省份 (Province)';
COMMENT ON COLUMN sys_log.city IS '城市 (City)';
COMMENT ON COLUMN sys_log.execution_time IS '執行時間(ms) (Execution Time ms)';
COMMENT ON COLUMN sys_log.browser IS '瀏覽器 (Browser)';
COMMENT ON COLUMN sys_log.browser_version IS '瀏覽器版本 (Browser Version)';
COMMENT ON COLUMN sys_log.os IS '終端系統 (Operating System)';
COMMENT ON COLUMN sys_log.create_by IS '建立人ID (Created By)';
COMMENT ON COLUMN sys_log.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_log.is_deleted IS '邏輯刪除標識(1-已刪除 0-未刪除) (Logical Delete: 1-Deleted 0-Not Deleted)';

-- ----------------------------
-- Table structure for gen_config
-- ----------------------------
DROP TABLE IF EXISTS gen_config CASCADE;
CREATE TABLE gen_config (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(100) NOT NULL,
    module_name VARCHAR(100),
    package_name VARCHAR(255) NOT NULL,
    business_name VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    author VARCHAR(50) NOT NULL,
    parent_menu_id BIGINT,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- Add unique constraint
CREATE UNIQUE INDEX uk_tablename ON gen_config(table_name);

-- Add comments
COMMENT ON TABLE gen_config IS '程式碼生成基礎配置表 (Code Generation Config Table)';
COMMENT ON COLUMN gen_config.table_name IS '表名 (Table Name)';
COMMENT ON COLUMN gen_config.module_name IS '模組名 (Module Name)';
COMMENT ON COLUMN gen_config.package_name IS '包名 (Package Name)';
COMMENT ON COLUMN gen_config.business_name IS '業務名 (Business Name)';
COMMENT ON COLUMN gen_config.entity_name IS '實體類名 (Entity Name)';
COMMENT ON COLUMN gen_config.author IS '作者 (Author)';
COMMENT ON COLUMN gen_config.parent_menu_id IS '上級選單ID，對應sys_menu的id (Parent Menu ID, refers to sys_menu.id)';
COMMENT ON COLUMN gen_config.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN gen_config.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN gen_config.is_deleted IS '是否刪除 (Is Deleted)';

-- ----------------------------
-- Table structure for gen_field_config
-- ----------------------------
DROP TABLE IF EXISTS gen_field_config CASCADE;
CREATE TABLE gen_field_config (
    id BIGSERIAL PRIMARY KEY,
    config_id BIGINT NOT NULL,
    column_name VARCHAR(100),
    column_type VARCHAR(50),
    column_length INTEGER,
    field_name VARCHAR(100) NOT NULL,
    field_type VARCHAR(100),
    field_sort INTEGER,
    field_comment VARCHAR(255),
    max_length INTEGER,
    is_required BOOLEAN,
    is_show_in_list BOOLEAN DEFAULT FALSE,
    is_show_in_form BOOLEAN DEFAULT FALSE,
    is_show_in_query BOOLEAN DEFAULT FALSE,
    query_type SMALLINT,
    form_type SMALLINT,
    dict_type VARCHAR(50),
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

-- Add index
CREATE INDEX idx_config_id ON gen_field_config(config_id);

-- Add comments
COMMENT ON TABLE gen_field_config IS '程式碼生成欄位配置表 (Code Generation Field Config Table)';
COMMENT ON COLUMN gen_field_config.config_id IS '關聯的配置ID (Related Config ID)';
COMMENT ON COLUMN gen_field_config.column_name IS '欄位名稱 (Column Name)';
COMMENT ON COLUMN gen_field_config.column_type IS '欄位型別 (Column Type)';
COMMENT ON COLUMN gen_field_config.column_length IS '欄位長度 (Column Length)';
COMMENT ON COLUMN gen_field_config.field_name IS '欄位名稱 (Field Name)';
COMMENT ON COLUMN gen_field_config.field_type IS '欄位型別 (Field Type)';
COMMENT ON COLUMN gen_field_config.field_sort IS '欄位排序 (Field Sort Order)';
COMMENT ON COLUMN gen_field_config.field_comment IS '欄位描述 (Field Description)';
COMMENT ON COLUMN gen_field_config.max_length IS '最大長度 (Max Length)';
COMMENT ON COLUMN gen_field_config.is_required IS '是否必填 (Is Required)';
COMMENT ON COLUMN gen_field_config.is_show_in_list IS '是否在列表顯示 (Show in List)';
COMMENT ON COLUMN gen_field_config.is_show_in_form IS '是否在表單顯示 (Show in Form)';
COMMENT ON COLUMN gen_field_config.is_show_in_query IS '是否在查詢條件顯示 (Show in Query)';
COMMENT ON COLUMN gen_field_config.query_type IS '查詢方式 (Query Type)';
COMMENT ON COLUMN gen_field_config.form_type IS '表單型別 (Form Type)';
COMMENT ON COLUMN gen_field_config.dict_type IS '字典型別 (Dictionary Type)';
COMMENT ON COLUMN gen_field_config.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN gen_field_config.update_time IS '更新時間 (Updated Time)';

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS sys_config CASCADE;
CREATE TABLE sys_config (
    id BIGSERIAL PRIMARY KEY,
    config_name VARCHAR(50) NOT NULL,
    config_key VARCHAR(50) NOT NULL,
    config_value VARCHAR(100) NOT NULL,
    remark VARCHAR(255),
    create_time TIMESTAMP,
    create_by BIGINT,
    update_time TIMESTAMP,
    update_by BIGINT,
    is_deleted SMALLINT DEFAULT 0 NOT NULL
);

-- Add comments
COMMENT ON TABLE sys_config IS '系統配置表 (System Config Table)';
COMMENT ON COLUMN sys_config.config_name IS '配置名稱 (Config Name)';
COMMENT ON COLUMN sys_config.config_key IS '配置key (Config Key)';
COMMENT ON COLUMN sys_config.config_value IS '配置值 (Config Value)';
COMMENT ON COLUMN sys_config.remark IS '備註 (Remark)';
COMMENT ON COLUMN sys_config.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_config.create_by IS '建立人ID (Created By)';
COMMENT ON COLUMN sys_config.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN sys_config.update_by IS '更新人ID (Updated By)';
COMMENT ON COLUMN sys_config.is_deleted IS '邏輯刪除標識(0-未刪除 1-已刪除) (Logical Delete: 0-Not Deleted 1-Deleted)';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO sys_config (id, config_name, config_key, config_value, remark, create_time, create_by, update_time, update_by, is_deleted) VALUES 
(1, '系統限流QPS', 'IP_QPS_THRESHOLD_LIMIT', '10', '單個IP請求的最大每秒查詢數（QPS）閾值Key', NOW(), 1, NULL, NULL, 0);

-- Reset sequence
SELECT setval('sys_config_id_seq', (SELECT MAX(id) FROM sys_config));

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS sys_notice CASCADE;
CREATE TABLE sys_notice (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(50),
    content TEXT,
    type SMALLINT NOT NULL,
    level VARCHAR(5) NOT NULL,
    target_type SMALLINT NOT NULL,
    target_user_ids VARCHAR(255),
    publisher_id BIGINT,
    publish_status SMALLINT DEFAULT 0,
    publish_time TIMESTAMP,
    revoke_time TIMESTAMP,
    create_by BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    update_by BIGINT,
    update_time TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

-- Add comments
COMMENT ON TABLE sys_notice IS '通知公告表 (Notice Table)';
COMMENT ON COLUMN sys_notice.title IS '通知標題 (Notice Title)';
COMMENT ON COLUMN sys_notice.content IS '通知內容 (Notice Content)';
COMMENT ON COLUMN sys_notice.type IS '通知型別（關聯字典編碼：notice_type） (Notice Type, refers to dict code: notice_type)';
COMMENT ON COLUMN sys_notice.level IS '通知等級（字典code：notice_level） (Notice Level, dict code: notice_level)';
COMMENT ON COLUMN sys_notice.target_type IS '目標型別（1: 全體, 2: 指定） (Target Type: 1-All, 2-Specified)';
COMMENT ON COLUMN sys_notice.target_user_ids IS '目標人ID集合（多個使用英文逗號,分割） (Target User IDs, comma separated)';
COMMENT ON COLUMN sys_notice.publisher_id IS '釋出人ID (Publisher ID)';
COMMENT ON COLUMN sys_notice.publish_status IS '釋出狀態（0: 未釋出, 1: 已釋出, -1: 已撤回） (Publish Status: 0-Unpublished, 1-Published, -1-Revoked)';
COMMENT ON COLUMN sys_notice.publish_time IS '釋出時間 (Publish Time)';
COMMENT ON COLUMN sys_notice.revoke_time IS '撤回時間 (Revoke Time)';
COMMENT ON COLUMN sys_notice.create_by IS '建立人ID (Created By)';
COMMENT ON COLUMN sys_notice.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_notice.update_by IS '更新人ID (Updated By)';
COMMENT ON COLUMN sys_notice.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN sys_notice.is_deleted IS '是否刪除（0: 未刪除, 1: 已刪除） (Is Deleted: 0-Not Deleted, 1-Deleted)';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO sys_notice (id, title, content, type, level, target_type, target_user_ids, publisher_id, publish_status, publish_time, revoke_time, create_by, create_time, update_by, update_time, is_deleted) VALUES 
(1, 'v2.12.0 新增系統日誌，訪問趨勢統計功能。', '<p>1. 訊息通知</p><p>2. 字典重構</p><p>3. 程式碼生成</p>', 1, 'L', 1, '2', 1, 1, NOW(), NOW(), 2, NOW(), 1, NOW(), 0),
(2, 'v2.13.0 新增選單搜尋。', '<p>1. 訊息通知</p><p>2. 字典重構</p><p>3. 程式碼生成</p>', 1, 'L', 1, '2', 1, 1, NOW(), NOW(), 2, NOW(), 1, NOW(), 0),
(3, 'v2.14.0 新增個人中心。', '<p>1. 訊息通知</p><p>2. 字典重構</p><p>3. 程式碼生成</p>', 1, 'L', 1, '2', 2, 1, NOW(), NOW(), 2, NOW(), 2, NOW(), 0),
(4, 'v2.15.0 登入頁面改造。', '<p>1. 訊息通知</p><p>2. 字典重構</p><p>3. 程式碼生成</p>', 1, 'L', 1, '2', 2, 1, NOW(), NOW(), 2, NOW(), 2, NOW(), 0),
(5, 'v2.16.0 通知公告、字典翻譯元件。', '<p>1. 訊息通知</p><p>2. 字典重構</p><p>3. 程式碼生成</p>', 1, 'L', 1, '2', 2, 1, NOW(), NOW(), 2, NOW(), 2, NOW(), 0),
(6, '系統將於本週六凌晨 2 點進行維護，預計維護時間為 2 小時。', '<p>1. 訊息通知</p><p>2. 字典重構</p><p>3. 程式碼生成</p>', 2, 'H', 1, '2', 2, 1, NOW(), NOW(), 2, NOW(), 2, NOW(), 0),
(7, '最近發現一些釣魚郵件，請大家提高警惕，不要點選陌生連結。', '<p>1. 訊息通知</p><p>2. 字典重構</p><p>3. 程式碼生成</p>', 3, 'L', 1, '2', 2, 1, NOW(), NOW(), 2, NOW(), 2, NOW(), 0),
(8, '國慶假期從 10 月 1 日至 10 月 7 日放假，共 7 天。', '<p>1. 訊息通知</p><p>2. 字典重構</p><p>3. 程式碼生成</p>', 4, 'L', 1, '2', 2, 1, NOW(), NOW(), 2, NOW(), 2, NOW(), 0),
(9, '公司將在 10 月 15 日舉辦新產品釋出會，敬請期待。', '公司將在 10 月 15 日舉辦新產品釋出會，敬請期待。', 5, 'H', 1, '2', 2, 1, NOW(), NOW(), 2, NOW(), 2, NOW(), 0),
(10, 'v2.16.1 版本釋出。', 'v2.16.1 版本修復了 WebSocket 重複連線導致的後臺執行緒阻塞問題，最佳化了通知公告。', 1, 'M', 1, '2', 2, 1, NOW(), NOW(), 2, NOW(), 2, NOW(), 0);

-- Reset sequence
SELECT setval('sys_notice_id_seq', (SELECT MAX(id) FROM sys_notice));

-- ----------------------------
-- Table structure for sys_user_notice
-- ----------------------------
DROP TABLE IF EXISTS sys_user_notice CASCADE;
CREATE TABLE sys_user_notice (
    id BIGSERIAL PRIMARY KEY,
    notice_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_read BIGINT DEFAULT 0,
    read_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP,
    is_deleted SMALLINT DEFAULT 0
);

-- Add comments
COMMENT ON TABLE sys_user_notice IS '使用者通知公告表 (User Notice Table)';
COMMENT ON COLUMN sys_user_notice.id IS 'id (ID)';
COMMENT ON COLUMN sys_user_notice.notice_id IS '公共通知id (Notice ID)';
COMMENT ON COLUMN sys_user_notice.user_id IS '使用者id (User ID)';
COMMENT ON COLUMN sys_user_notice.is_read IS '讀取狀態（0: 未讀, 1: 已讀） (Read Status: 0-Unread, 1-Read)';
COMMENT ON COLUMN sys_user_notice.read_time IS '閱讀時間 (Read Time)';
COMMENT ON COLUMN sys_user_notice.create_time IS '建立時間 (Created Time)';
COMMENT ON COLUMN sys_user_notice.update_time IS '更新時間 (Updated Time)';
COMMENT ON COLUMN sys_user_notice.is_deleted IS '邏輯刪除(0: 未刪除, 1: 已刪除) (Logical Delete: 0-Not Deleted, 1-Deleted)';

-- ----------------------------
-- Records of sys_user_notice
-- ----------------------------
INSERT INTO sys_user_notice (id, notice_id, user_id, is_read, read_time, create_time, update_time, is_deleted) VALUES 
(1, 1, 2, 1, NULL, NOW(), NOW(), 0),
(2, 2, 2, 1, NULL, NOW(), NOW(), 0),
(3, 3, 2, 1, NULL, NOW(), NOW(), 0),
(4, 4, 2, 1, NULL, NOW(), NOW(), 0),
(5, 5, 2, 1, NULL, NOW(), NOW(), 0),
(6, 6, 2, 1, NULL, NOW(), NOW(), 0),
(7, 7, 2, 1, NULL, NOW(), NOW(), 0),
(8, 8, 2, 1, NULL, NOW(), NOW(), 0),
(9, 9, 2, 1, NULL, NOW(), NOW(), 0),
(10, 10, 2, 1, NULL, NOW(), NOW(), 0);

-- Reset sequence
SELECT setval('sys_user_notice_id_seq', (SELECT MAX(id) FROM sys_user_notice));

-- ----------------------------
-- Add Foreign Key Constraints (Optional)
-- ----------------------------
-- You can uncomment these if you want to enforce referential integrity

-- ALTER TABLE sys_role_menu ADD CONSTRAINT fk_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role(id);
-- ALTER TABLE sys_role_menu ADD CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id);
-- ALTER TABLE sys_user_role ADD CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
-- ALTER TABLE sys_user_role ADD CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id);
-- ALTER TABLE sys_user_notice ADD CONSTRAINT fk_user_notice_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
-- ALTER TABLE sys_user_notice ADD CONSTRAINT fk_user_notice_notice FOREIGN KEY (notice_id) REFERENCES sys_notice(id);
-- ALTER TABLE gen_field_config ADD CONSTRAINT fk_field_config_gen FOREIGN KEY (config_id) REFERENCES gen_config(id);

-- ----------------------------
-- Indexes for Performance (Optional)
-- ----------------------------
-- Create additional indexes based on your query patterns
-- CREATE INDEX idx_sys_user_status ON sys_user(status);
-- CREATE INDEX idx_sys_menu_parent ON sys_menu(parent_id);
-- CREATE INDEX idx_sys_role_status ON sys_role(status);
-- CREATE INDEX idx_sys_notice_status ON sys_notice(publish_status);

-- End of script
