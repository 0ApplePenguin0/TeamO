$(document).ready(function() {
    // 양식 선택 시 해당 양식을 로드하여 폼 렌더링
    $('#templateId').change(function() {
        let templateId = $(this).val();
        if (templateId) {
            $.ajax({
                url: '/api/form-templates/' + templateId,
                method: 'GET',
                success: function(template) {
                    let formRenderOptions = {
                        formData: JSON.parse(template.formStructure),
                        dataType: 'json'
                    };
                    $('#form-render').empty(); // 기존 양식 제거
                    $('#form-render').formRender(formRenderOptions);
                },
                error: function() {
                    alert('양식 로드에 실패했습니다.');
                }
            });
        }
    });

    // 부서 선택 시 팀 목록 로드
    $('#department').change(function() {
        let departmentId = $(this).val();
        if (departmentId) {
            $.ajax({
                url: '/api/departments/' + departmentId + '/teams',
                method: 'GET',
                success: function(teams) {
                    $('#team').empty();
                    $('#team').append('<option value="" disabled selected>팀을 선택하세요</option>');
                    $.each(teams, function(index, team) {
                        $('#team').append('<option value="' + team.teamId + '">' + team.teamName + '</option>');
                    });
                },
                error: function() {
                    alert('팀 목록 로드에 실패했습니다.');
                }
            });
        }
    });

    // 팀 선택 시 멤버 목록 로드
    $('#team').change(function() {
        let teamId = $(this).val();
        if (teamId) {
            $.ajax({
                url: '/api/teams/' + teamId + '/members',
                method: 'GET',
                success: function(members) {
                    // 각 결재자 셀렉트 박스를 초기화하고 멤버 목록을 추가
                    $('#approver1, #approver2, #approver3').each(function() {
                        $(this).empty();
                        $(this).append('<option value="" disabled selected>결재자를 선택하세요</option>');
                        $.each(members, function(index, member) {
                            $(this).append('<option value="' + member.memberId + '">' + member.memberName + '</option>');
                        }.bind(this));
                    });
                },
                error: function() {
                    alert('멤버 목록 로드에 실패했습니다.');
                }
            });
        }
    });

    // 폼 제출 시 동적 폼의 데이터를 JSON 형태로 저장
    $('form').on('submit', function(event) {
        // 첫 번째 결재자 선택 여부 확인
        let approver1 = $('#approver1').val();
        if (!approver1) {
            alert('결재자 1을 선택해주세요.');
            event.preventDefault();
            return false;
        }

        // 중복 결재자 선택 여부 확인
        let approver2 = $('#approver2').val();
        let approver3 = $('#approver3').val();
        let approvers = [approver1, approver2, approver3].filter(Boolean);
        let uniqueApprovers = [...new Set(approvers)];

        if (approvers.length !== uniqueApprovers.length) {
            alert('동일한 결재자를 중복 선택할 수 없습니다.');
            event.preventDefault();
            return false;
        }

        // 폼 데이터를 JSON 형태로 저장
        let formData = {};
        $('#form-render').find('input, select, textarea').each(function() {
            let name = $(this).attr('name');
            let value;
            if ($(this).attr('type') === 'checkbox') {
                value = $(this).is(':checked');
            } else if ($(this).attr('type') === 'radio') {
                if ($(this).is(':checked')) {
                    value = $(this).val();
                }
            } else {
                value = $(this).val();
            }

            if (name && value !== undefined && value !== '') {
                formData[name] = value;
            }
        });

        // 숨겨진 'content' 필드에 JSON 문자열로 변환한 데이터를 설정
        $('#content').val(JSON.stringify(formData));

        // 중복 제출 방지: 제출 버튼 비활성화
        $('button[type=submit]').prop('disabled', true);
    });
});