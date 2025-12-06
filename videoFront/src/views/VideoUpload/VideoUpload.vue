<!-- 视频上传页面组件 -->
<template>
    <div class="upload-container" style="width: 800px; margin: 50px auto; padding: 30px; border: 1px solid #eee; border-radius: 8px; background: #fff;">
        <h2 style="margin: 0 0 30px 0; color: #333; text-align: center;">视频上传</h2>
        
        <!-- 上传表单 -->
        <form @submit.prevent="handleUpload" style="display: flex; flex-direction: column; gap: 20px;">
            <!-- 视频标题 -->
            <div class="form-item">
                <label style="display: block; margin-bottom: 8px; font-size: 14px; color: #666;">视频标题 <span style="color: red;">*</span></label>
                <input
                    type="text"
                    v-model="videoForm.title"
                    placeholder="请输入视频标题"
                    style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px;"
                    required
                >
            </div>

            <!-- 视频描述 -->
            <div class="form-item">
                <label style="display: block; margin-bottom: 8px; font-size: 14px; color: #666;">视频描述</label>
                <textarea
                    v-model="videoForm.description"
                    placeholder="可选：输入视频简介"
                    style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; min-height: 80px; resize: vertical;"
                ></textarea>
            </div>

            <!-- 视频文件选择 -->
            <div class="form-item">
                <label style="display: block; margin-bottom: 8px; font-size: 14px; color: #666;">选择视频 <span style="color: red;">*</span></label>
                <input
                    type="file"
                    accept="video/*"  
                    @change="handleFileChange"
                    style="width: 100%; padding: 10px; border: 1px dashed #ddd; border-radius: 4px; cursor: pointer;"
                    required
                ><!-- 只允许选择视频文件 -->
                <p v-if="selectedFile" style="margin: 8px 0 0 0; font-size: 13px; color: #888;">
                    已选择：{{ selectedFile.name }}（大小：{{ formatFileSize(selectedFile.size) }}）
                </p>
                <p style="margin: 8px 0 0 0; font-size: 12px; color: #999;">
                    支持格式：MP4、MOV、AVI等，单个文件不超过100MB
                </p>
            </div>

            <!-- 上传按钮 -->
            <button
                type="submit"
                :disabled="isUploading"
                style="padding: 12px; background: #3498db; color: #fff; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; transition: background 0.3s;"
            >
                <span v-if="isUploading">上传中...</span>
                <span v-else>确认上传</span>
            </button>
        </form>
    </div>
</template>

<script setup>
    import { ref } from 'vue';
    import { useRouter } from 'vue-router';
    import request from '@/api/request'; // 复用已有的请求封装

    const router = useRouter();

    // 表单数据
    const videoForm = ref({
        title: '',
        description: ''
    });

    // 选中的文件
    const selectedFile = ref(null);
    // 上传状态
    const isUploading = ref(false);

    // 处理文件选择
    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            // 限制文件大小（100MB）
            const maxSize = 100 * 1024 * 1024; // 100MB
            if (file.size > maxSize) {
                alert('文件超过100MB，请选择更小的视频！');
                e.target.value = ''; // 清空选择
                selectedFile.value = null;
                return;
            }
            selectedFile.value = file;
        }
    };

    // 格式化文件大小（B → KB/MB）
    const formatFileSize = (size) => {
        if (size < 1024) {
            return size + ' B';
        } else if (size < 1024 * 1024) {
            return (size / 1024).toFixed(2) + ' KB';
        } else {
            return (size / (1024 * 1024)).toFixed(2) + ' MB';
        }
    };

    // 处理上传
    const handleUpload = async () => {
        // 表单验证
        if (!videoForm.value.title.trim()) {
            alert('请输入视频标题！');
            return;
        }
        if (!selectedFile.value) {
            alert('请选择要上传的视频！');
            return;
        }

        try {
            isUploading.value = true;

            /**  构建FormData（文件上传必须用这个格式，支持二进制文件传输）
            *  键值对（不是普通json，是表单格式）保存视频数据
            *  键和后端变量名一致，可以被SpringMVC自动按变量名转为后端对应的变量
            */ 
            const formData = new FormData();
            formData.append('title', videoForm.value.title.trim());
            formData.append('description', videoForm.value.description.trim());
            formData.append('file', selectedFile.value); // 文件名和后端接口参数一致

            // 调用上传接口（自动带token，无需手动加）
            const res = await request.post('/video/upload', formData, {
                // 上传文件需要设置Content-Type为multipart/form-data（axios会自动处理，这里可选）
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                // 控制台显示上传进度（如需展示进度条，可加这个）
                onUploadProgress: (progressEvent) => {
                    const progress = (progressEvent.loaded / progressEvent.total) * 100;
                    console.log(`上传进度：${progress.toFixed(2)}%`);
                }
            });

            // 上传成功，跳转到个人主页
            alert(res.message || '上传成功！');
            router.push('/profile'); // 跳个人主页查看视频
        } catch (err) {
            console.error('上传失败：', err);
            alert(err.message || '上传失败，请重试！');
        } finally {
            isUploading.value = false;
        }
    };
</script>