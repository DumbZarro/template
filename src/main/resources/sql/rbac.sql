INSERT INTO public.perm (value, uri, description, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES ('download', 'aaa', '下载权限', '_system', '2025-03-11 19:08:51.712288 +00:00', '_system', '2025-03-11 19:08:51.712288 +00:00', false);
INSERT INTO public.perm (value, uri, description, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES ('delete', 'bbb', '删除权限', '_system', '2025-03-11 19:09:39.557748 +00:00', '_system', '2025-03-11 19:09:39.557748 +00:00', false);
INSERT INTO public.perm (value, uri, description, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES ('select', 'ccc', '查看权限', '_system', '2025-03-11 19:09:39.557748 +00:00', '_system', '2025-03-11 19:09:39.557748 +00:00', false);

INSERT INTO public.role (name, value, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES ('admin', '管理员', 'system', '2025-03-11 19:07:34.099214 +00:00', 'system', '2025-03-11 19:07:34.099214 +00:00', false);
INSERT INTO public.role (name, value, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES ('sale', '销售', 'system', '2025-03-11 19:07:34.099214 +00:00', 'system', '2025-03-11 19:07:34.099214 +00:00', false);

INSERT INTO public.role_perm_rel (role_id, perm_id, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES (1, 1, '_system', '2025-03-11 19:10:42.419462 +00:00', '_system', '2025-03-11 19:10:42.419462 +00:00', false);
INSERT INTO public.role_perm_rel (role_id, perm_id, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES (1, 2, '_system', '2025-03-11 19:10:42.419462 +00:00', '_system', '2025-03-11 19:10:42.419462 +00:00', false);
INSERT INTO public.role_perm_rel (role_id, perm_id, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES (1, 3, '_system', '2025-03-11 19:10:42.419462 +00:00', '_system', '2025-03-11 19:10:42.419462 +00:00', false);
INSERT INTO public.role_perm_rel (role_id, perm_id, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES (2, 3, '_system', '2025-03-11 19:10:42.419462 +00:00', '_system', '2025-03-11 19:10:42.419462 +00:00', false);

INSERT INTO public.user_basic_info (id, email, password, name, avatar_url, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES (7305303151626686465, 'email', 'password', 'dzl', 'avatar_url', '_system', '2025-03-11 19:06:37.316745 +00:00', '_system', '2025-03-11 19:06:37.316745 +00:00', false);

INSERT INTO public.user_role_rel (user_id, role_id, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES (7305303151626686465, 1, '_system', '2025-03-11 19:07:59.810856 +00:00', '_system', '2025-03-11 19:07:59.810856 +00:00', false);
INSERT INTO public.user_role_rel (user_id, role_id, sys_created_by, sys_created_time, sys_updated_by, sys_updated_time, sys_deleted) VALUES (7305303151626686465, 2, '_system', '2025-03-11 19:08:04.892967 +00:00', '_system', '2025-03-11 19:08:04.892967 +00:00', false);


SELECT u.*, r.id as rid, r.name, r.value, pm.id pid, pm.value pvalue, pm.description
from user_basic_info u
         left join user_role_rel ur on ur.user_id = u.id
         left join role r on r.id = ur.role_id
         left join role_perm_rel rp on r.id = rp.role_id
         left join perm pm on pm.id = rp.perm_id
WHERE u.id = 7305303151626686465;

